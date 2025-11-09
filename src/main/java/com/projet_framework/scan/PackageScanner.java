package com.projet_framework.scan;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.projet_framework.annotation.mapper.AnnotationMapping;
import com.projet_framework.annotation.method.Get;
import com.projet_framework.annotation.type.Controller;

public class PackageScanner {

    public static List<AnnotationMapping> process(String packageName) {
        List<AnnotationMapping> handlers = new ArrayList<>();
        List<Class<?>> classes = getClassesInPackage(packageName);

        for (Class<?> clazz : classes) {
            Controller controller = clazz.getAnnotation(Controller.class);
            String baseUrl = (controller != null) ? controller.url() : null;

            if (controller != null) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    Get get = method.getAnnotation(Get.class);
                    String methodUrl = get.url();
                    String fullUrl = (baseUrl != null) ? normalizeUrl(baseUrl + methodUrl) : normalizeUrl(methodUrl);
                    handlers.add(new AnnotationMapping(clazz, method, fullUrl));
                }
            } else {
                System.out.println("Class : " + clazz.getName() + "is not annotated");
            }
        }
        return handlers;
    }

    private static String normalizeUrl(String url) {
        // Simple normalization: ensure starts with /, no double //
        url = url.replaceAll("//+", "/");
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }

    private static List<Class<?>> getClassesInPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
                        if (files != null) {
                            for (File file : files) {
                                String className = packageName + '.'
                                        + file.getName().substring(0, file.getName().length() - 6);
                                classes.add(Class.forName(className));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

}
