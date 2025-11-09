package com.projet_framework.annotation.mapper;

import java.lang.reflect.Method;

public class AnnotationMapping {
    private Class<?> clazz;
    private Method method;
    private String url;
    
    
    public AnnotationMapping(Class<?> clazz, Method method, String url) {
        this.clazz = clazz;
        this.method = method;
        this.url = url;
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
     @Override
    public String toString() {
        return "Handler{" +
                "clazz=" + clazz.getName() +
                ", method=" + (method != null ? method.getName() : "None") +
                ", Url='" + url + '\'' +
                '}';
    }
}
