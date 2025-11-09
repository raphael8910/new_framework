package com.projet_framework.annotation.mapper;

import java.util.HashMap;
import java.util.Map;

public class URLMapper {
    private Map<String, AnnotationMapping> mappings = new HashMap<>();

    public void map(AnnotationMapping mapping) throws Exception{
        String url = mapping.getUrl();
        if (mappings.containsKey(url)) {
            throw new Exception("Duplicate mapping for " + url);
        }
        mappings.put(url, mapping);
    }
    public AnnotationMapping get(String url) {
        return mappings.get(url);
    }
}