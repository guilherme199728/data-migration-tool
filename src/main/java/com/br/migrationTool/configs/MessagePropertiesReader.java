package com.br.migrationTool.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessagePropertiesReader {
    @Bean
    private MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        return messageSource;
    }

    public String getMessage(String tagProperties) {
        return messageSource().getMessage(tagProperties, null, new Locale("BR"));
    }
}
