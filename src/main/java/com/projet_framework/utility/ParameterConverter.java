package com.projet_framework.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;

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
                    return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
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
            throw new Exception("Format de date invalide pour '" + value
                    + "'. Formats acceptés: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy");
        }
    }

    public static <T> T getAttributeWithType(HttpServletRequest request, String attributeName, Class<T> targetType)
            throws Exception {
        Object attribute = request.getAttribute(attributeName);

        if (attribute == null) {
            return (T) getDefaultValue(targetType);
        }

        if (targetType.isInstance(attribute)) {
            return targetType.cast(attribute);
        }

        String stringValue = attribute.toString();
        return targetType.cast(convert(stringValue, targetType));
    }

    public static String getStringAttribute(HttpServletRequest req, String name) throws Exception {
        return getAttributeWithType(req, name, String.class);
    }

    public static Integer getIntAttribute(HttpServletRequest req, String name) throws Exception {
        return getAttributeWithType(req, name, Integer.class);
    }

    public static Double getDoubleAttribute(HttpServletRequest req, String name) throws Exception {
        return getAttributeWithType(req, name, Double.class);
    }
    public static Boolean getBooleanAttribute(HttpServletRequest req, String name) throws Exception {
        return getAttributeWithType(req, name, Boolean.class);
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