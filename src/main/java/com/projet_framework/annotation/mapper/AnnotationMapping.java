package com.projet_framework.annotation.mapper;

import java.lang.reflect.Method;

public class AnnotationMapping {
    private Class<?> clazz;
    private Method method;
    private String url;
    private String httpMethod;  

    public AnnotationMapping(Class<?> clazz, Method method, String url, String httpMethod) {
        this.clazz = clazz;
        this.method = method;
        this.url = url;
        this.httpMethod = httpMethod;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
    
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    @Override
    public String toString() {
        return "AnnotationMapping{" +
                "clazz=" + clazz.getName() +
                ", method=" + (method != null ? method.getName() : "None") +
                ", url='" + url + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }
}