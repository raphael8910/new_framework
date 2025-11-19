package com.projet_framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import com.projet_framework.annotation.mapper.AnnotationMapping;
import com.projet_framework.annotation.mapper.URLMapper;
import com.projet_framework.scan.PackageScanner;
import com.projet_framework.utility.ModelView;
import com.projet_framework.utility.ParameterConverter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet{

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

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
       service(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
       service(req, resp);
    }
    
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter out = resp.getWriter();
        
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();   
        String resourcePath = requestURI.substring(contextPath.length()); 

        // Vérifier si c'est une ressource statique
        if (getServletContext().getResource(resourcePath) != null) {
            try {
                getServletContext().getRequestDispatcher(resourcePath).forward(req, resp);
                return;
            } catch (Exception e) {
                // Continue si ce n'est pas une ressource statique
            }
        }
        
        URLMapper urlMapper = (URLMapper) getServletContext().getAttribute("urlMapper");
        
        if (urlMapper == null) {
            resp.setContentType("text/plain; charset=UTF-8");
            out.write("Erreur: URLMapper non initialisé");
            return;
        }
        
        AnnotationMapping mapping = urlMapper.get(resourcePath);
        
        if (mapping == null) {
            resp.setContentType("text/plain; charset=UTF-8");
            out.write("Erreur: Aucun mapping trouvé pour l'URL: " + resourcePath);
            return;
        }
        
        try {
            // Instancier le controller
            Class<?> controllerClass = mapping.getClazz();
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            
            // Récupérer la méthode et ses paramètres
            Method method = mapping.getMethod();
            Parameter[] parameters = method.getParameters();
            
            // Préparer les arguments pour l'invocation de la méthode
            Object[] arguments = new Object[parameters.length];
            
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                String paramName = param.getName();
                Class<?> paramType = param.getType();
                
                // Récupérer la valeur du paramètre depuis la requête HTTP
                String paramValue = req.getParameter(paramName);
                
                System.out.println("VALEUR -> "+paramName+ " :"+ paramValue);
                // Convertir la valeur au type attendu
                try {
                    arguments[i] = ParameterConverter.convert(paramValue, paramType);
                } catch (Exception e) {
                    resp.setContentType("text/plain; charset=UTF-8");
                    out.write("Erreur de conversion pour le paramètre '" + paramName + "': " + e.getMessage());
                    return;
                }
            }
            
            // Invoquer la méthode avec les arguments
            Object result = method.invoke(controllerInstance, arguments);
            
            // Traiter le résultat selon son type
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
                    // Ajouter "/" au début si absent
                    if (!page.startsWith("/")) {
                        page = "/" + page;
                    }
                    
                    // Ajouter tous les objets du model comme attributs de la requête
                    for (String key : modelView.getModel().keySet()) {
                        req.setAttribute(key, modelView.getModel().get(key));
                    }
                    
                    // Vérifier si la page existe
                    if (getServletContext().getResource(page) != null) {
                        getServletContext().getRequestDispatcher(page).forward(req, resp);
                    } else {
                        resp.setContentType("text/plain; charset=UTF-8");
                        out.write("Erreur: La page '" + page + "' n'existe pas");
                    }
                }
                
            } else {
                // Pour String, int, et autres types basiques
                resp.setContentType("text/plain; charset=UTF-8");
                out.write(result.toString());
            }
            
        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            out.write("Erreur lors de l'exécution de la méthode: " + e.getMessage());
            e.printStackTrace();
        }
    }
}