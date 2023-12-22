package com.wex.gateways.silvio.purchase.i18n;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class MessageService {

    @Autowired
    ResourceBundleMessageSource messageSource;

    @PostConstruct
    private void init() {
        LocaleContextHolder.setDefaultLocale(Locale.ENGLISH);
    }

    public String getMessage(String key){
    	return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String key, String arg){
    	return messageSource.getMessage(key, new Object[]{arg}, LocaleContextHolder.getLocale());
    }
    
    public String getMessage(String key, List<String> args){
        return messageSource.getMessage(key, args.toArray(), LocaleContextHolder.getLocale());
    }
}
