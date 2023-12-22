package com.wex.gateways.silvio.purchase.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;

import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig{
	

    @Autowired
    ResourceBundleMessageSource messageSource;

    @PostConstruct
    public void init(){
        String welcome = messageSource.getMessage("welcome", null, Locale.ENGLISH);

        System.out.println();
        System.out.println(welcome);
        System.out.println();
    }
    
    
}