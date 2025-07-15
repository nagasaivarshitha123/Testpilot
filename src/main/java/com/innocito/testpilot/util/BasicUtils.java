package com.innocito.testpilot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BasicUtils {

    @Autowired
    private MessageSource messageSource;

    public String getLocalizedMessage(String key, Object[] objects) {
        return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
    }
}