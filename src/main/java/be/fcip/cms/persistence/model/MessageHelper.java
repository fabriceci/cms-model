package be.fcip.cms.persistence.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageHelper {

    @Autowired
    private MessageSource messageSource;

    public String message(String key){

        return this.messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
