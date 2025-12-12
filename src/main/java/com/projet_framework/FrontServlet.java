package com.projet_framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.projet_framework.annotation.mapper.AnnotationMapping;
import com.projet_framework.annotation.mapper.MappingMatch;
import com.projet_framework.annotation.mapper.URLMapper;
import com.projet_framework.annotation.method.JSON;
import com.projet_framework.annotation.parameter.EntityBody;
import com.projet_framework.annotation.parameter.PathVariable;
import com.projet_framework.annotation.parameter.RequestParam;
import com.projet_framework.scan.PackageScanner;
import com.projet_framework.utility.JsonResponse;
import com.projet_framework.utility.ModelView;
import com.projet_framework.utility.ParameterConverter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        String packageName = getInitParameter("controllers-package");

        if (packageName == null || packageName.isEmpty()) {
            throw new ServletException("Le paramètre 'controllers-package' est requis dans web.xml");
        }

        System.out.println("Scanning package: " + packageName);

        List<AnnotationMapping> mappings = PackageScanner.process(packageName);

        URLMapper urlMapper = new URLMapper();
        for (AnnotationMapping mapping : mappings) {
            try {
                urlMapper.map(mapping);
                System.out.println("Mapped: " + mapping);
            } catch (Exception e) {
                System.err.println("Erreur lors du mapping: " + e.getMessage());
            }
        }

        getServletContext().setAttribute("urlMapper", urlMapper);
        System.out.println("URLMapper initialisé avec " + mappings.size() + " mappings");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        service(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        service(req, resp);
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String resourcePath = requestURI.substring(contextPath.length());

        if (getServletContext().getResource(resourcePath) != null) {
            try {
                getServletContext().getRequestDispatcher(resourcePath).forward(req, resp);
                return;
            } catch (Exception e) {
            }
        }

        URLMapper urlMapper = (URLMapper) getServletContext().getAttribute("urlMapper");

        if (urlMapper == null) {
            resp.setContentType("text/plain; charset=UTF-8");
            out.write("Erreur: URLMapper non initialisé");
            return;
        }

        String httpMethod = req.getMethod();

        MappingMatch match = urlMapper.match(resourcePath, httpMethod);

        if (match == null) {
            List<String> availableMethods = urlMapper.getAvailableMethodsForUrl(resourcePath);

            resp.setContentType("text/plain; charset=UTF-8");
            if (availableMethods.isEmpty()) {
                out.write("Erreur: Aucun mapping trouvé pour l'URL: " + resourcePath);
            } else {
                out.write("Erreur: L'URL " + resourcePath + " existe mais ne supporte pas la méthode " + httpMethod +
                        ".\nMéthodes disponibles: " + availableMethods);
            }
            return;
        }

        AnnotationMapping mapping = match.getMapping();
        Map<String, String> pathVariables = match.getPathVariables();

        Method method = mapping.getMethod();
        boolean isJsonResponse = method.isAnnotationPresent(JSON.class);

        try {
            Class<?> controllerClass = mapping.getClazz();
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            Parameter[] parameters = method.getParameters();
            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String paramValue = null;
                String paramName = null;
                Class<?> paramType = param.getType();

                if (param.isAnnotationPresent(EntityBody.class)) {
                    Class<?> entityClass = param.getType();

                    try {
                        Object entityInstance = entityClass.getDeclaredConstructor().newInstance();

                        Field[] fields = entityClass.getDeclaredFields();

                        for (Field field : fields) {
                            field.setAccessible(true);

                            String fieldName = field.getName();
                            String fieldValue = req.getParameter(fieldName);

                            if (fieldValue != null) {
                                Object convertedValue = ParameterConverter.convert(fieldValue, field.getType());
                                field.set(entityInstance, convertedValue);
                                System.out.println("EntityBody -> " + fieldName + " : " + convertedValue);
                            }
                        }

                        arguments[i] = entityInstance;
                        break;
                    } catch (Exception e) {
                        if (isJsonResponse) {
                            JsonResponse jsonResponse = new JsonResponse(404, "error", null, 
                                "Erreur lors de la création de l'entité: " + e.getMessage());
                            resp.setContentType("application/json; charset=UTF-8");
                            out.write(jsonResponse.toJson());
                            return;
                        } else {
                            resp.setContentType("text/plain; charset=UTF-8");
                            out.write("Erreur lors de la création de l'entité: " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                    }

                } else if (paramType == Map.class) {
                    Map<String, Object> resultMap = new HashMap<>();
                    Enumeration<String> paramNames = req.getParameterNames();

                    while (paramNames.hasMoreElements()) {
                        String paramName1 = paramNames.nextElement();
                        Object value = req.getParameter(paramName1);
                        resultMap.put(paramName1, value);
                    }
                    arguments[i] = resultMap;
                    break;
                } else if (param.isAnnotationPresent(PathVariable.class)) {
                    PathVariable annotation = param.getAnnotation(PathVariable.class);
                    paramName = annotation.name();
                    paramValue = pathVariables.get(paramName);

                    if (paramValue == null) {
                        if (isJsonResponse) {
                            JsonResponse jsonResponse = new JsonResponse(404, "error", null, 
                                "Variable de chemin '" + paramName + "' non trouvée dans l'URL");
                            resp.setContentType("application/json; charset=UTF-8");
                            out.write(jsonResponse.toJson());
                            return;
                        } else {
                            resp.setContentType("text/plain; charset=UTF-8");
                            out.write("Erreur: Variable de chemin '" + paramName + "' non trouvée dans l'URL");
                            return;
                        }
                    }

                    System.out.println("PathVariable -> " + paramName + " : " + paramValue);

                } else if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam annotation = param.getAnnotation(RequestParam.class);
                    paramName = annotation.paramName();
                    paramValue = req.getParameter(paramName);

                    System.out.println("RequestParam -> " + paramName + " : " + paramValue);

                } else {
                    paramName = param.getName();
                    paramValue = req.getParameter(paramName);

                    System.out.println("Parameter -> " + paramName + " : " + paramValue);
                }

                try {
                    arguments[i] = ParameterConverter.convert(paramValue, paramType);
                } catch (Exception e) {
                    if (isJsonResponse) {
                        JsonResponse jsonResponse = new JsonResponse(404, "error", null, 
                            "Erreur de conversion pour le paramètre '" + paramName + "': " + e.getMessage());
                        resp.setContentType("application/json; charset=UTF-8");
                        out.write(jsonResponse.toJson());
                        return;
                    } else {
                        resp.setContentType("text/plain; charset=UTF-8");
                        out.write("Erreur de conversion pour le paramètre '" + paramName + "': " + e.getMessage());
                        return;
                    }
                }
            }

            Object result = method.invoke(controllerInstance, arguments);

            if (isJsonResponse) {
                Object data = null;

                if (result instanceof ModelView) {
                    ModelView modelView = (ModelView) result;
                    data = modelView.getModel();
                } else {
                    data = result;
                }

                JsonResponse jsonResponse = new JsonResponse(200, "success", data, null);
                resp.setContentType("application/json; charset=UTF-8");
                out.write(jsonResponse.toJson());
                return;
            }

            if (result == null) {
                resp.setContentType("text/plain; charset=UTF-8");
                out.write("Erreur: La méthode a retourné null");

            } else if (result instanceof ModelView) {
                ModelView modelView = (ModelView) result;
                String page = modelView.getPage();

                if (page == null || page.isEmpty()) {
                    resp.setContentType("text/plain; charset=UTF-8");
                    out.write("Erreur: Le ModelView ne contient pas de page");
                } else {
                    if (!page.startsWith("/")) {
                        page = "/" + page;
                    }

                    for (String key : modelView.getModel().keySet()) {
                        req.setAttribute(key, modelView.getModel().get(key));
                    }

                    if (getServletContext().getResource(page) != null) {
                        getServletContext().getRequestDispatcher(page).forward(req, resp);
                    } else {
                        resp.setContentType("text/plain; charset=UTF-8");
                        out.write("Erreur: La page '" + page + "' n'existe pas");
                    }
                }

            } else {
                resp.setContentType("text/plain; charset=UTF-8");
                out.write(result.toString());
            }

        } catch (Exception e) {
            if (isJsonResponse) {
                JsonResponse jsonResponse = new JsonResponse(404, "error", null, 
                    "Erreur lors de l'exécution de la méthode: " + e.getMessage());
                resp.setContentType("application/json; charset=UTF-8");
                out.write(jsonResponse.toJson());
            } else {
                resp.setContentType("text/plain; charset=UTF-8");
                out.write("Erreur lors de l'exécution de la méthode: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}