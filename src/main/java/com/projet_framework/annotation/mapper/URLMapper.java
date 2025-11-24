package com.projet_framework.annotation.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLMapper {
    private Map<String, List<AnnotationMapping>> exactMappings = new HashMap<>();
    private List<AnnotationMapping> patternMappings = new ArrayList<>();

    public void map(AnnotationMapping mapping) throws Exception {
        String url = mapping.getUrl();
        
        if (url.contains("{")) {
            patternMappings.add(mapping);
        } else {
            if (!exactMappings.containsKey(url)) {
                exactMappings.put(url, new ArrayList<>());
            }
            
            List<AnnotationMapping> mappingsForUrl = exactMappings.get(url);
            for (AnnotationMapping existing : mappingsForUrl) {
                if (existing.getHttpMethod().equals(mapping.getHttpMethod())) {
                    throw new Exception("Duplicate mapping for " + mapping.getHttpMethod() + " " + url);
                }
            }
            
            mappingsForUrl.add(mapping);
        }
    }
    public MappingMatch match(String url, String httpMethod) {
        List<AnnotationMapping> mappingsForUrl = exactMappings.get(url);
        if (mappingsForUrl != null) {
            for (AnnotationMapping mapping : mappingsForUrl) {
                if (mapping.getHttpMethod().equals(httpMethod)) {
                    return new MappingMatch(mapping, new HashMap<>());
                }
            }
        }
        
        for (AnnotationMapping mapping : patternMappings) {
            if (mapping.getHttpMethod().equals(httpMethod)) {
                Map<String, String> pathVars = matchPattern(mapping.getUrl(), url);
                if (pathVars != null) {
                    return new MappingMatch(mapping, pathVars);
                }
            }
        }
        
        return null;
    }

    private Map<String, String> matchPattern(String pattern, String url) {
        String[] patternSegments = pattern.split("/");
        String[] urlSegments = url.split("/");
        
        if (patternSegments.length != urlSegments.length) {
            return null;
        }
        
        Map<String, String> pathVariables = new HashMap<>();
        
        for (int i = 0; i < patternSegments.length; i++) {
            String patternSegment = patternSegments[i];
            String urlSegment = urlSegments[i];
            
            if (patternSegment.startsWith("{") && patternSegment.endsWith("}")) {
                String varName = patternSegment.substring(1, patternSegment.length() - 1);
                pathVariables.put(varName, urlSegment);
            } else {
                if (!patternSegment.equals(urlSegment)) {
                    return null;
                }
            }
        }
        
        return pathVariables;
    }

    public List<String> getAvailableMethodsForUrl(String url) {
        List<String> methods = new ArrayList<>();
        
        List<AnnotationMapping> mappingsForUrl = exactMappings.get(url);
        if (mappingsForUrl != null) {
            for (AnnotationMapping mapping : mappingsForUrl) {
                methods.add(mapping.getHttpMethod());
            }
        }
        
        for (AnnotationMapping mapping : patternMappings) {
            Map<String, String> pathVars = matchPattern(mapping.getUrl(), url);
            if (pathVars != null) {
                methods.add(mapping.getHttpMethod());
            }
        }
        
        return methods;
    }

    public AnnotationMapping get(String url) {
        MappingMatch match = match(url, "GET");
        return match != null ? match.getMapping() : null;
    }
}