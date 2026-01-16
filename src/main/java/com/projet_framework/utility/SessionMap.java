package com.projet_framework.utility;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpSession;

/**
 * Map qui synchronise automatiquement avec HttpSession
 * Toute modification du Map est reflétée dans la session HTTP
 */
public class SessionMap implements Map<String, Object> {
    
    private HttpSession session;
    
    public SessionMap(HttpSession session) {
        this.session = session;
    }

    @Override
    public int size() {
        int count = 0;
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributeNames.nextElement();
            count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return !session.getAttributeNames().hasMoreElements();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return session.getAttribute(key.toString()) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object attrValue = session.getAttribute(name);
            if (attrValue != null && attrValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        if (key == null) return null;
        return session.getAttribute(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        Object oldValue = session.getAttribute(key);
        session.setAttribute(key, value);
        return oldValue;
    }

    @Override
    public Object remove(Object key) {
        if (key == null) return null;
        Object oldValue = session.getAttribute(key.toString());
        session.removeAttribute(key.toString());
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.removeAttribute(attributeNames.nextElement());
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new java.util.HashSet<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            keys.add(attributeNames.nextElement());
        }
        return keys;
    }

    @Override
    public Collection<Object> values() {
        Collection<Object> values = new java.util.ArrayList<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            values.add(session.getAttribute(attributeNames.nextElement()));
        }
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entries = new java.util.HashSet<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            entries.add(new HashMap.SimpleEntry<>(name, value));
        }
        return entries;
    }
}