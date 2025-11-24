package com.projet_framework.annotation.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLMapper {
    private Map<String, AnnotationMapping> exactMappings = new HashMap<>();
    private List<AnnotationMapping> patternMappings = new ArrayList<>();

    public void map(AnnotationMapping mapping) throws Exception {
        String url = mapping.getUrl();
        
        if (url.contains("{")) {
            patternMappings.add(mapping);
        } else {
            if (exactMappings.containsKey(url)) {
                throw new Exception("Duplicate mapping for " + url);
            }
            exactMappings.put(url, mapping);
        }
    }

    public MappingMatch match(String url) {
        if (exactMappings.containsKey(url)) {
            return new MappingMatch(exactMappings.get(url), new HashMap<>());
        }
        
        for (AnnotationMapping mapping : patternMappings) {
            Map<String, String> pathVars = matchPattern(mapping.getUrl(), url);
            if (pathVars != null) {
                return new MappingMatch(mapping, pathVars);
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

    public AnnotationMapping get(String url) {
        MappingMatch match = match(url);
        return match != null ? match.getMapping() : null;
    }
}