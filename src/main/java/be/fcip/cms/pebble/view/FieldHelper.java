package be.fcip.cms.pebble.view;

import be.fcip.cms.model.PageData;
import be.fcip.cms.model.TemplateField;
import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.service.ICmsFieldService;
import be.fcip.cms.persistence.service.IPageService;
import be.fcip.cms.persistence.service.IPageTemplateService;
import be.fcip.cms.service.IPeebleService;
import be.fcip.cms.util.CmsContentUtils;
import be.fcip.cms.util.CmsStringUtils;
import be.fcip.cms.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FieldHelper {

    @Autowired private IPeebleService peebleService;
    @Autowired private ICmsFieldService cmsFieldService;
    @Autowired private IPageService pageService;
    @Autowired private IPageTemplateService pageTemplateService;

    public String render(PageContentEntity content) throws Exception{
        return render(content,  false);
    }

    public String render(PageContentEntity content, boolean isRevision) throws Exception {
        PageEntity page = pageService.findPageCached(content.getPage().getId());
        PageTemplateEntity template = pageTemplateService.findCached(page.getTemplate().getId());
        PageData pageData =  CmsContentUtils.parseStringToPageDate(content.getData());
        StringBuilder builder = new StringBuilder();
        Map<String, Object> model;

        Gson gson = new Gson();
        List<TemplateField> fields = gson.fromJson(template.getFields(), new TypeToken<ArrayList<TemplateField>>(){}.getType());
        Map<String, CmsFieldEntity> fieldMap = cmsFieldService.findAllCmsFieldCached().stream()
                .collect(Collectors.toMap(CmsFieldEntity::getName, field -> field));


        for (TemplateField contentTemplateFieldset : fields) {

            model = new HashMap<>();
            String namespace = StringUtils.isEmpty(contentTemplateFieldset.getNamespace()) ? "_" : contentTemplateFieldset.getNamespace() + '_';
            model.put("np",  namespace);

            CmsFieldEntity fieldEntity = fieldMap.get(contentTemplateFieldset.getType());
            Map<String, Object> inputsMap = new HashMap<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat(CmsUtils.DATE_FORMAT);


            String finalName = namespace + CmsStringUtils.toSlug(fieldEntity.getCodeName());
            Map<String, Object> inputMap = new HashMap<>();
            inputMap.put("title", contentTemplateFieldset.getTitle());
            if(isRevision){
                inputMap.put("name", "_rev_" + finalName);
            } else {
                inputMap.put("name", finalName);
            }

            inputMap.put("hint", fieldEntity.getHint());
            //inputMap.put("validation", cmsFieldDataEntity.getValidation());
            //inputMap.put("default", cmsFieldDataEntity.getDefaultValue());
            inputMap.put("isArray", contentTemplateFieldset.isArray());
            if(pageData!= null) {
                String inputType = fieldEntity.getType();
                // isArray
                if (fieldEntity.isArray() && contentTemplateFieldset.isArray()) {
                    switch (inputType) {
                        case "date":
                            Date[] dates = pageData.getDataDateArray().get(finalName);
                            if (dates != null) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pageData.getDataDateArray().get(finalName).length; i++) {
                                    if (i != 0) {
                                        sb.append(",");
                                    }
                                    sb.append(dateFormatter.format(dates[i]));
                                }
                                inputMap.put("data", sb.toString());
                            }
                            break;
                        case "integer":
                            inputMap.put("data", pageData.getDataIntegerArray().get(finalName));
                            break;
                        case "double":
                            inputMap.put("data", pageData.getDataDoubleArray().get(finalName));
                            break;
                        case "boolean":
                            inputMap.put("data", pageData.getDataBooleanArray().get(finalName));
                            break;
                        case "json":
                            inputMap.put("data", pageData.getDataMapArray().get(finalName));
                            break;
                        default:
                            inputMap.put("data", pageData.getDataStringArray().get(finalName));
                            break;
                    }
                    // Not Array
                } else {
                    switch (inputType) {
                        case "date":
                            Date d = pageData.getDataDate().get(finalName);
                            if (d != null) {
                                inputMap.put("data", dateFormatter.format(d));
                            }

                            break;
                        case "integer":
                            inputMap.put("data", pageData.getDataInteger().get(finalName));
                            break;
                        case "double":
                            inputMap.put("data", pageData.getDataDouble().get(finalName));
                            break;
                        case "boolean":
                            inputMap.put("data", pageData.getDataBoolean().get(finalName));
                            break;
                        case "json":
                            inputMap.put("data", pageData.getDataMap().get(finalName));

                            break;
                        default:
                            inputMap.put("data", pageData.getDataString().get(finalName));
                            break;
                    }
                }
            }


            // avoid "null"
            inputMap.putIfAbsent("data", "");
            inputsMap.put(finalName, inputMap);

            // end for
            model.put("inputs", inputsMap);

            // peebleService.fillModelMap(model, null);
            builder.append(peebleService.parseString(fieldEntity.getTemplate(), model, "field_" + fieldEntity.getId()));
        }

        return builder.toString();
    }
}
