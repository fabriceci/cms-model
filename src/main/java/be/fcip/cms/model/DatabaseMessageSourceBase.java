package be.fcip.cms.model;

import be.fcip.cms.persistence.service.IMessageService;
import be.fcip.cms.util.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class DatabaseMessageSourceBase extends AbstractMessageSource {

    @Autowired private IMessageService messageService;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return createMessageFormat(getText(code, locale), locale);
    }

    private String getText(String code, Locale locale) {
        Map<String, Map<String, String>> all = messageService.mapOfTranslationCached();

        Map<String, String> codeMap = all.get(code);
        String result = null;
        if (codeMap != null) {
            result = codeMap.get(locale.toString());
            if (result == null && locale.toString().length() > 2) {
                result = codeMap.get(locale.getLanguage());
            }
        }

        if(result == null && ApplicationUtils.isDev){
            log.debug("Message not found for code : " + code);
        }
        return result != null ? result : code;
    }
}
