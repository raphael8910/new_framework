package com.projet_framework;

import java.io.IOException;
import java.util.List;

import com.projet_framework.annotation.mapper.AnnotationMapping;
import com.projet_framework.annotation.mapper.URLMapper;
import com.projet_framework.scan.PackageScanner;

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

        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();   
        String resourcePath = requestURI.substring(contextPath.length()); 

        System.out.println("Resource path: " + resourcePath);

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
            resp.getWriter().write("Erreur: URLMapper non initialisé");
            return;
        }
        
        AnnotationMapping mapping = urlMapper.get(resourcePath);
        
        resp.setContentType("text/plain; charset=UTF-8");
        
        if (mapping != null) {
            resp.getWriter().write("URL trouvée: " + resourcePath + "\n");
            resp.getWriter().write("Classe: " + mapping.getClazz().getName() + "\n");
            resp.getWriter().write("Méthode: " + mapping.getMethod().getName());
        } else {
            resp.getWriter().write("Voici l'URL reçu : " + resourcePath);
        }
    }
}