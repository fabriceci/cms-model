package be.fcip.cms.pebble.view;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.model.PageData;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.model.TemplateField;
import be.fcip.cms.persistence.service.ICmsFieldService;
import be.fcip.cms.service.IPeebleService;
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

    @Autowired
    private IPeebleService peebleService;
    @Autowired
    private ICmsFieldService cmsFieldService;

    public String renderField(PageTemplateEntity template, PageData pageData) throws Exception{
        return renderField(template, pageData, false);
    }

    public String renderField(PageTemplateEntity template, PageData pageData, boolean isRevision) throws Exception {
        if(template == null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        Map<String, Object> model;

        Gson gson = new Gson();
        List<TemplateField> fields = gson.fromJson(template.getFields(), new TypeToken<ArrayList<TemplateField>>(){}.getType());
        Map<String, CmsFieldEntity> fieldMap = cmsFieldService.findAllCmsField().stream()
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
                    if (inputType.equals("date")) {
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
                    }
                    else if(inputType.equals("integer")){
                        inputMap.put("data", pageData.getDataIntegerArray().get(finalName));
                    }
                    else if(inputType.equals("double")){
                        inputMap.put("data", pageData.getDataDoubleArray().get(finalName));
                    }
                    else if(inputType.equals("boolean")){
                        inputMap.put("data", pageData.getDataBooleanArray().get(finalName));
                    }
                    else if(inputType.equals("json")){
                        inputMap.put("data", pageData.getDataMapArray().get(finalName));
                    } else {
                        inputMap.put("data", pageData.getDataStringArray().get(finalName));
                    }
                    // Not Array
                } else {
                    if (inputType.equals("date")) {
                        Date d = pageData.getDataDate().get(finalName);
                        if (d != null) {
                            inputMap.put("data", dateFormatter.format(d));
                        }

                    }
                    else if(inputType.equals("integer")){
                        inputMap.put("data", pageData.getDataInteger().get(finalName));
                    }
                    else if(inputType.equals("double")){
                        inputMap.put("data", pageData.getDataDouble().get(finalName));
                    }
                    else if(inputType.equals("boolean")){
                        inputMap.put("data", pageData.getDataBoolean().get(finalName));
                    }
                    else if(inputType.equals("json")){
                        inputMap.put("data", pageData.getDataMap().get(finalName));

                    } else {
                        inputMap.put("data", pageData.getDataString().get(finalName));
                    }
                }
            }


            // avoid "null"
            inputMap.putIfAbsent("data", "");
            inputsMap.put(finalName, inputMap);

            // end for
            model.put("inputs", inputsMap);

            // peebleService.fillModelMap(model, null);
            builder.append(peebleService.parseBlock(fieldEntity.getBlockEntity(), model));
        }

        return builder.toString();
    }
}
