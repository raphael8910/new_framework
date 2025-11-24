package com.projet_framework.annotation.mapper;

import java.util.Map;

public class MappingMatch {
    private AnnotationMapping mapping;
    private Map<String, String> pathVariables;

    public MappingMatch(AnnotationMapping mapping, Map<String, String> pathVariables) {
        this.mapping = mapping;
        this.pathVariables = pathVariables;
    }

    public AnnotationMapping getMapping() {
        return mapping;
    }

    public void setMapping(AnnotationMapping mapping) {
        this.mapping = mapping;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    @Override
    public String toString() {
        return "MappingMatch{" +
                "mapping=" + mapping +
                ", pathVariables=" + pathVariables +
                '}';
    }
}