package be.fcip.cms.util;

import be.fcip.cms.model.JsonErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CmsJsonUtils {

    public static String toJson(Object o){
        Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
        return gson.toJson(o);
    }

    public static boolean isJSONValid(String JSON_STRING) {
        Gson gson = new Gson();
        try {
            gson.fromJson(JSON_STRING, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


    public static JsonErrorResponse getJsonErrorResponse(MessageSource messageSource, Locale locale){
        return getJsonErrorResponse(messageSource.getMessage("an error occured", null, locale), messageSource.getMessage("error.general", null, locale));
    }

    public static JsonErrorResponse getJsonErrorResponse(String title, String detail){
        JsonErrorResponse jsonErrorResponse = new JsonErrorResponse();

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("title", CmsStringUtils.capitalizeFirstLetter(title));
        if(!StringUtils.isEmpty(detail)) {
            errorMap.put("detail", CmsStringUtils.capitalizeFirstLetter(detail));
        }
        jsonErrorResponse.getErrors().add(errorMap);
        return jsonErrorResponse;
    }
}
