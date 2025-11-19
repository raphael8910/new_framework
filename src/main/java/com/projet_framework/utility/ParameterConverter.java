package com.projet_framework.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ParameterConverter {
  
    public static Object convert(String value, Class<?> targetType) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return getDefaultValue(targetType);
        }
        
        value = value.trim();
        
        try {
            if (targetType == String.class) {
                return value;
            }
            
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(value);
            }
            
            if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(value);
            }
            
            if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            }
            
            if (targetType == LocalDate.class) {
                try {
                    return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
                } catch (DateTimeParseException e1) {
                    try {
                        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (DateTimeParseException e2) {
                        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    }
                }
            }
            
            throw new Exception("Type non supporté: " + targetType.getName());
            
        } catch (NumberFormatException e) {
            throw new Exception("Impossible de convertir '" + value + "' en " + targetType.getSimpleName());
        } catch (DateTimeParseException e) {
            throw new Exception("Format de date invalide pour '" + value + "'. Formats acceptés: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy");
        }
    }
    

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) {
            return 0;
        }
        if (type == double.class) {
            return 0.0;
        }
        if (type == boolean.class) {
            return false;
        }
        return null;
    }
}