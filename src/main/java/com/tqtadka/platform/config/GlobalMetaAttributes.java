package com.tqtadka.platform.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class GlobalMetaAttributes {

    @ModelAttribute("canonicalUrl")
    public String canonicalUrl(HttpServletRequest request) {

        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replaceQuery(null)
                .scheme("https")
                .build()
                .toUriString();
    }
}