package be.fcip.cms.util;

import be.fcip.cms.model.PageData;
import be.fcip.cms.model.TemplateField;
import be.fcip.cms.persistence.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Slf4j
public class CmsContentUtils {

    public static List<TaxonomyEntity> taxonomyByType(PageEntity content, String type){
        List<TaxonomyEntity> result = new ArrayList<>();
        for (TaxonomyEntity t : content.getTaxonomyEntities()) {
            if(t.getType().equals(type)){
                result.add(t);
            }
        }
        return result;
    }

    public static String computeSlug(final PageEntity content, final PageContentEntity contentData, final String locale, final PageEntity parent, final boolean forceLang, WebsiteEntity websiteEntity) {
        if(websiteEntity==null || content == null || contentData == null || StringUtils.isEmpty(contentData.getSlug()) || StringUtils.isEmpty(locale)){
            throw new IllegalArgumentException("Content, contentData, slug and locale can't be null");
        }
        if(!contentData.getSlug().startsWith("/")){
            contentData.setSlug("/" + contentData.getSlug());
        }

        String slug = StringUtils.trimToEmpty(computeSlugWithSlashes(content, contentData, locale, parent, forceLang, websiteEntity.getSlug())).replaceAll("/+", "/");
        return (slug.length() > 1) ? slug.replaceAll("/+$", "") : slug;
    }

    public static Set<Pattern> computeDynamicUrl(String pattern, boolean langForcedInUrl, String langKey) throws PatternSyntaxException {
        StringBuilder builder = new StringBuilder();
        String[] split = pattern.split(" !or! ");
        Set<Pattern> patterns = new HashSet<>();
        for (String s : split) {
            if(langForcedInUrl){
                builder.append("/").append(langKey);
            }
            builder.append(s);
            patterns.add(Pattern.compile(builder.toString()));
        }
        return patterns;
    }

    public static boolean displayable(PageEntity content){
        boolean displayable = content.isEnabled();
        if(content.getEndDate() != null && content.getEndDate().compareTo(LocalDateTime.now()) < 0){
            displayable = false;
        }
        return displayable;
    }

    public static boolean displayable(PageContentEntity contentData){
        return contentData.isEnabled();
    }

    public static String computeSlugWithSlashes(final PageEntity content, final PageContentEntity contentData, final String locale, final PageEntity parent, final boolean forceLang, String websiteSlug) {
        boolean malFormedSlug = contentData.getSlug().charAt(0) != '/';
        boolean needBeginSlash = !StringUtils.isEmpty(websiteSlug) && websiteSlug.charAt(0) != '/';
        if(malFormedSlug){  log.error("Slug malformed (missing /) : contentData with id : " + contentData.getId()); }
        if(needBeginSlash){
            websiteSlug = "/" + websiteSlug;
        }
        String slug = malFormedSlug ? '/' + contentData.getSlug() : contentData.getSlug();

        if (parent == null) {
            if (forceLang) {
                return websiteSlug + "/" + locale + slug;
            } else {
                return websiteSlug + slug;
            }

        } else {
            final PageContentEntity parentContentData = parent.getContentMap().get(locale);

            return websiteSlug + parentContentData.getComputedSlug() + "/" + contentData.getSlug();
        }
    }

    public static HashMap<String, Object> parseData(String pageDataString) {
        HashMap<String, Object> data = new HashMap<>();
        if(StringUtils.isEmpty(pageDataString)){
            return data;
        }

        PageData pageData = parseStringToPageDate(pageDataString);

        if(pageData == null){
            log.error("page data is null with the parameter : " + pageDataString);
            throw new IllegalArgumentException("page data is null with the parameter : " + pageDataString);
        }

        data.putAll(pageData.getDataBoolean());
        data.putAll(pageData.getDataBooleanArray());
        data.putAll(pageData.getDataDate());
        data.putAll(pageData.getDataDateArray());
        data.putAll(pageData.getDataDouble());
        data.putAll(pageData.getDataDoubleArray());
        data.putAll(pageData.getDataInteger());
        data.putAll(pageData.getDataIntegerArray());
        data.putAll(pageData.getDataString());
        data.putAll(pageData.getDataStringArray());
        data.putAll(pageData.getDataMap());
        data.putAll(pageData.getDataMapArray());
        return data;
    }

    public static PageData parseStringToPageDate(String pageDataString) {
        Gson gson = new GsonBuilder().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
        return gson.fromJson(pageDataString, PageData.class);
    }

    public static PageData fillData(PageData pageData, PageTemplateEntity template, List<CmsFieldEntity> fieldsDefinition, HttpServletRequest request) throws IOException, ParseException {

        if (pageData == null) {
            pageData = new PageData();
        }

        Gson gson = new Gson();

        List<TemplateField> fields = gson.fromJson(template.getFields(), new TypeToken<ArrayList<TemplateField>>(){}.getType());
        Map<String, CmsFieldEntity> fieldMap = fieldsDefinition.stream()
                .collect(Collectors.toMap(CmsFieldEntity::getName, field -> field));

        SimpleDateFormat dateFormatter = new SimpleDateFormat(CmsUtils.DATE_FORMAT);
        for (TemplateField ctf : fields) {

            Map<String, Object> inputsMap = new HashMap<>();
            String namespace = StringUtils.isEmpty(ctf.getNamespace()) ? "_" : ctf.getNamespace() + '_';
            CmsFieldEntity fieldEntity = fieldMap.get(ctf.getType());
            String finalName = namespace + fieldEntity.getCodeName();
            String type = fieldEntity.getType();
            boolean isArray = ctf.isArray() && fieldEntity.isArray();
            // TYPE DATE
            if (type.equals("date")) {
                if (isArray) {
                    String dateArrayParam = request.getParameter(finalName);
                    if(StringUtils.isEmpty(dateArrayParam)){
                        pageData.getDataDateArray().remove(finalName);
                    }
                    else {
                        String[] stringDateArray = dateArrayParam.split(",");
                        Date[] dateArray = new Date[stringDateArray.length];
                        for (int i = 0; i < stringDateArray.length; i++) {
                            dateArray[i] = dateFormatter.parse(stringDateArray[i]);
                        }
                        pageData.getDataDateArray().put(finalName, dateArray);
                    }
                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataDate().remove(finalName);
                    } else {
                        try {
                            pageData.getDataDate().put(finalName, dateFormatter.parse(param));
                        } catch (ParseException e){
                            pageData.getDataDate().remove(finalName);
                        }
                    }
                }
                // Type INTEGER
            } else if (type.equals("integer")) {
                if (isArray) {
                    final String[] stringArray = CmsArrayUtils.cleanArray(request.getParameterValues(finalName));
                    if(stringArray == null){
                        pageData.getDataIntegerArray().remove(finalName);
                    } else {
                        final Integer[] ints = new Integer[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            try {
                                ints[i] = Integer.parseInt(stringArray[i]);
                            } catch (NumberFormatException e){}
                        }
                        if(ints.length == 0){
                            pageData.getDataIntegerArray().remove(finalName);
                        } else{
                            pageData.getDataIntegerArray().put(finalName, ints);
                        }

                    }
                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataInteger().remove(finalName);
                    } else {
                        try {
                            pageData.getDataInteger().put(finalName, Integer.parseInt(param));
                        } catch( NumberFormatException e){
                            pageData.getDataInteger().remove(finalName);
                        }
                    }
                }
                // TYPE DOUBLE
            } else if (type.equals("double")) {
                if (isArray) {
                    final String[] stringArray = CmsArrayUtils.cleanArray(request.getParameterValues(finalName));
                    if(stringArray == null) {
                        pageData.getDataDoubleArray().remove(finalName);
                    }
                    else{
                        final Double[] doubles = new Double[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            try {
                                doubles[i] = Double.parseDouble(stringArray[i]);
                            } catch (NumberFormatException e){}
                        }
                        if(doubles.length == 0) {
                            pageData.getDataDoubleArray().remove(finalName);
                        } else {
                            pageData.getDataDoubleArray().put(finalName, doubles);
                        }
                    }
                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataDouble().remove(finalName);
                    } else {
                        try {
                            pageData.getDataDouble().put(finalName, Double.parseDouble(param));
                        } catch (NumberFormatException e){
                            pageData.getDataDouble().remove(finalName);
                        }
                    }
                }
                // TYPE BOOLEAN
            } else if (type.equals("boolean")) {
                if (isArray) {
                    final String[] stringArray = CmsArrayUtils.cleanArray(request.getParameterValues(finalName));
                    if(stringArray == null){
                        pageData.getDataBooleanArray().remove(finalName);
                    } else {
                        final Boolean[] booleans = new Boolean[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            booleans[i] = Boolean.parseBoolean(stringArray[i]);
                        }
                        pageData.getDataBooleanArray().put(finalName, booleans);
                    }
                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataBoolean().remove(finalName);
                    } else {
                        pageData.getDataBoolean().put(finalName, Boolean.parseBoolean(param));
                    }
                }
                // TYPE JSON
            } else if (type.equals("json")) {
                Type gsonMapType = new TypeToken<Map>(){}.getType();
                if (isArray) {
                    final String[] stringArray = CmsArrayUtils.cleanArray(request.getParameterValues(finalName));
                    if(stringArray == null){
                        pageData.getDataMapArray().remove(finalName);
                    } else {
                        List<Map> result = new ArrayList<>();
                        for (String s : stringArray) {
                            try {
                                Map item = gson.fromJson(s, gsonMapType);
                                result.add(item);
                            } catch(JsonSyntaxException e){}
                        }
                        if(result.isEmpty()){
                            pageData.getDataMapArray().remove(finalName);
                        } else{
                            pageData.getDataMapArray().put(finalName, result);
                        }
                    }
                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataMap().remove(finalName);
                    } else {
                        try{
                            Map item = gson.fromJson(param, gsonMapType);
                            pageData.getDataMap().put(finalName, item);
                        } catch (JsonSyntaxException e){
                            pageData.getDataMap().remove(finalName);
                        }
                    }
                }
            }
            // TYPE STRING
            else {
                if (isArray) {
                    String[] parameterValues = CmsArrayUtils.cleanArray(request.getParameterValues(finalName));
                    if(parameterValues == null){
                        pageData.getDataStringArray().remove(finalName);
                    } else {
                        pageData.getDataStringArray().put(finalName, parameterValues);
                    }

                } else {
                    String param = request.getParameter(finalName);
                    if(StringUtils.isEmpty(param) || param.equals("null")) {
                        pageData.getDataString().remove(finalName);
                    }
                    else {
                        pageData.getDataString().put(finalName, param);
                    }
                }
            }
        }

        return pageData;
    }

    public static PageData fillData(PageTemplateEntity template,  List<CmsFieldEntity> fieldsDefinition, HttpServletRequest request) throws IOException, ParseException {
        return fillData(null, template, fieldsDefinition, request);
    }

    public static Map<String, List<PageFileEntity>> filesByGroupMap(PageContentEntity data, String type){
        final String default_group = "default";
        Map<String, List<PageFileEntity>> result = new LinkedHashMap<>();
        if(data == null || data.getContentFiles() == null) return null;
        for (PageFileEntity f : data.getContentFiles()) {
            if(f.getFileType().equals(type)){
                String group = f.getGroupName();
                if(group == null) group = default_group;
                List<PageFileEntity> list = result.get(group);
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(f);
                result.put(group, list);
            }
        }
        return result;
    }

    public static List<PageFileEntity> filesList(PageContentEntity data, String type){

        List<PageFileEntity> contentFiles = data.getContentFiles();
        List<PageFileEntity> result = new ArrayList<>();
        for (PageFileEntity contentFile : contentFiles) {
            if(contentFile.getFileType().equals(type)) {
                result.add(contentFile);
            }
        }
        return result;
    }
}
