package be.fcip.cms.service;

import be.fcip.cms.exception.ResourceNotFoundException;
import be.fcip.cms.persistence.model.*;
import be.fcip.cms.persistence.service.IAppParamService;
import be.fcip.cms.persistence.service.IBlockService;
import be.fcip.cms.persistence.service.IPageService;
import be.fcip.cms.persistence.service.IPageTemplateService;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsContentUtils;
import be.fcip.cms.util.CmsSecurityUtils;
import be.fcip.cms.util.CmsUtils;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class RenderPageServiceImpl implements IRenderPageService {

    @Autowired
    private IPageService contentService;
    @Autowired
    private IPageTemplateService contentTemplateService;
    @Autowired
    private IAppParamService appParamService;
    @Autowired
    private IBlockService blockService;
    @Autowired
    private IPeebleService peebleService;
    @Autowired
    private CacheManager cacheManager;

    @Override
    public String renderPage(HttpServletRequest request, HttpServletResponse response, PageEntity content, ModelMap model) throws IOException, PebbleException, ServletException {

        Locale locale = LocaleContextHolder.getLocale();
        if(content == null) throw new IllegalArgumentException();
        if(!ApplicationUtils.websites.containsKey(content.getWebsite().getId())) throw new IllegalArgumentException();
        if(CmsSecurityUtils.uriIsAdmin(request))  throw new ResourceNotFoundException();
        WebsiteEntity websiteEntity = ApplicationUtils.websites.get(content.getWebsite().getId());
        RequestContextHolder.getRequestAttributes().setAttribute("websiteId", websiteEntity.getId(), RequestAttributes.SCOPE_REQUEST);
        model.put("websiteId", websiteEntity.getId());
        if(appParamService.isMaintenance()){
            model.put("maintenance", true);
        }

        PageContentEntity contentData= null;
        PageTemplateEntity contentTemplateDto = null;

        String templateName = "";

        contentData = content.getContentMap().get(locale.toString());
        contentTemplateDto = contentTemplateService.find(content.getTemplate().getId());
        templateName = contentTemplateDto.getName().toLowerCase();
        model.put("template", contentTemplateDto);
        model.put("templateName", contentTemplateDto.getName());

        // cache check
        UserEntity currentUser = CmsSecurityUtils.getCurrentUser();
        Cache.ValueWrapper cacheWrapper = null;
        // TO DO : testé si on est en dev!
        String cacheKey = content.getId() + "_" + locale.toString();
        if((currentUser == null) && contentTemplateDto.isFullCache()){
            cacheWrapper = cacheManager.getCache("pageFull").get(cacheKey);
        } else if((currentUser == null) && contentTemplateDto.isShortCache()){
            cacheWrapper = cacheManager.getCache("pageShort").get(cacheKey);
        }
        if(cacheWrapper != null && cacheWrapper.get() != null){
            return (String)cacheWrapper.get();
        }

        if (!CmsContentUtils.displayable(content) || !CmsContentUtils.displayable(contentData) || contentTemplateDto.getName().equals("folder")) {
            throw new ResourceNotFoundException();
        }

        if(contentService.contentIsPrivate(content)){
            if(!CmsSecurityUtils.hasRole("ROLE_MEMBER")) {
                //response.sendRedirect("/login");
                new HttpSessionRequestCache().saveRequest(request, response);
                new LoginUrlAuthenticationEntryPoint("/" + locale.toString() + "/login").commence(request, response, new InsufficientAuthenticationException("Need member role"));
                return null;
            }
        }

        /*
        Example check role
        if(!CmsUtils.hasRoles(contentService.getRoleForContent(content))){
            throw new AccessDeniedException("you don't have the required privileges to perform this action");
        }*/

        HashMap<String, Object> data = null;
        if (!StringUtils.isEmpty(contentData.getData())) {
            data = CmsContentUtils.parseData(contentData.getData());

            if(templateName.equals("link")){
                String URL = (String) data.get("_text");
                response.sendRedirect(URL);
                return null;
            }

            model.put("data", data);
        }

        // SEO FIELDS (defined here to be overridable in IModelExtension hook)
        model.put("title", contentData.getTitle());
        model.put(IAppParamService.PARAM_SEO_IMAGE, appParamService.getParam(IAppParamService.PARAM_SEO_IMAGE));
        fillSeo(model, contentData, data);

        model.put("contentData", contentData);
        model.put("content", content);

        peebleService.fillModelMap(model, request);

        // Pas grave pour les perfs car les blocks seront dans le cache
        BlockEntity master = null;
        master = blockService.find(ApplicationUtils.websites.get(content.getWebsite().getId()).getMaster().getId());

        model.put("main",  peebleService.parseBlock(contentTemplateDto.getBlock(), model));

        StringBuilder include_top = new StringBuilder();
        StringBuilder include_bottom = new StringBuilder();

        if(!StringUtils.isEmpty(contentTemplateDto.getIncludeTop())){
            include_top.append(contentTemplateDto.getIncludeTop());
        }
        if(!StringUtils.isEmpty(content.getIncludeTop())){
            include_top.append('\n').append(content.getIncludeTop());
        }

        if(!StringUtils.isEmpty(contentTemplateDto.getIncludeBottom())){
            include_bottom.append(contentTemplateDto.getIncludeBottom());
        }
        if(!StringUtils.isEmpty(content.getIncludeBottom())){
            include_bottom.append('\n').append(content.getIncludeBottom());
        }

        if(!StringUtils.isEmpty(include_top)){
            model.put("include_top", peebleService.parseString(include_top.toString(), model) );
        }
        if(!StringUtils.isEmpty(include_bottom)) {
            model.put("include_bottom", peebleService.parseString(include_bottom.toString(), model));
        }

        final String result = peebleService.parseBlock(master, model);
        if((currentUser == null) && contentTemplateDto.isFullCache()){
            cacheManager.getCache("pageFull").put(cacheKey, result);
        } else if((currentUser == null) && contentTemplateDto.isShortCache()){
            cacheManager.getCache("pageShort").put(cacheKey, result);
        }
        return result;
    }

    private static String[] APP_PARAMS_META_NAME = {"seo_tags", "seo_description", "seo_title", "seo_h1"};
    private void fillSeo(ModelMap model, PageContentEntity contentData, HashMap<String, Object> data) {
        Map<String, String> paramsMap = appParamService.getParams();
        // DIRTY HACK - the map is singleton, I add the title in it to use in replaceTokenByParam
        paramsMap.put("title", contentData.getTitle());
        for (String paramName : APP_PARAMS_META_NAME) {
            // 1. Check if the page override the default value
            if(data.containsKey(paramName) && !StringUtils.isEmpty((String)data.get(paramName))){
                model.put(paramName, data.get(paramName));
            } else { // 2. Look at the default SEO values
                String result = appParamService.getParam(paramName, contentData.getLanguage());
                // 3. If we have a default parameter and not seo_tags (that not need processing)
                if(!paramName.equals("seo_tags") && !StringUtils.isEmpty(result)) {
                    result = appParamService.replaceTokenByParam(result, contentData.getLanguage());
                }
                model.put(paramName, result);

            }
        }
    }

}
