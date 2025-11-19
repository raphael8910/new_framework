package com.projet_framework.utility;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String page;

    private Map<String, Object>  model = new HashMap<>();

    public ModelView() {
    }

    public ModelView(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public void ajouterObjet(String key, Object value){
        model.put(key, value);
    }
}
