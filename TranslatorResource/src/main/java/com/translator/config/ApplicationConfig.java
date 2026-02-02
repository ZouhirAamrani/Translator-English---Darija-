package com.translator.config;

import com.translator.filter.AuthenticationFilter;
import com.translator.resource.TranslatorResource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Register authentication filter
        classes.add(AuthenticationFilter.class);
        classes.add(TranslatorResource.class); // wtf 
        
        return classes;
    }
}