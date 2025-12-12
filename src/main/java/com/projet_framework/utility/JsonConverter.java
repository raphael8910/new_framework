package com.projet_framework.utility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonConverter {

    public static String objectToJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        // Cas des types primitifs et String
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        // Cas des Collections (List, Set, etc.)
        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }

        // Cas des tableaux
        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        // Cas des Maps
        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }

        // Cas des objets custom (avec reflection)
        return customObjectToJson(obj);
    }

    private static String collectionToJson(Collection<?> collection) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(",");
            }
            sb.append(objectToJson(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String arrayToJson(Object array) {
        StringBuilder sb = new StringBuilder("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(objectToJson(java.lang.reflect.Array.get(array, i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
            sb.append(objectToJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String customObjectToJson(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (!first) {
                    sb.append(",");
                }
                sb.append("\"").append(field.getName()).append("\":");
                sb.append(objectToJson(value));
                first = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}