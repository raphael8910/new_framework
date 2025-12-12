package com.projet_framework.utility;

import java.util.Collection;

public class JsonResponse {
    private String status;
    private int code;
    private Object data;
    private Integer count;
    private String error;

    public JsonResponse(int code, String status, Object data, String error) {
        this.code = code;
        this.status = status;
        this.data = data;
        this.error = error;
        
        // Calculer count si data est une collection ou un tableau
        if (data instanceof Collection) {
            this.count = ((Collection<?>) data).size();
        } else if (data != null && data.getClass().isArray()) {
            this.count = java.lang.reflect.Array.getLength(data);
        }
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        
        sb.append("\"status\":\"").append(status).append("\",");
        sb.append("\"code\":").append(code).append(",");
        
        if (count != null) {
            sb.append("\"count\":").append(count).append(",");
        }
        
        sb.append("\"data\":");
        sb.append(JsonConverter.objectToJson(data)).append(",");
        
        sb.append("\"error\":");
        if (error == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(escapeJson(error)).append("\"");
        }
        
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String str) {
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