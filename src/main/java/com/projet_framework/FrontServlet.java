package com.projet_framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/")
public class FrontServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = url.substring(contextPath.length());

        // Empêcher l'accès aux répertoires protégés
        if (path.startsWith("/WEB-INF") || path.startsWith("/META-INF")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Vérifier si le fichier existe et n'est pas le servlet lui-même
        String realPath = getServletContext().getRealPath(path);
        if (realPath != null) {
            File file = new File(realPath);
            if (file.exists() && !file.isDirectory()) {
                request.getRequestDispatcher(path).forward(request, response);
                return;
            }
        }

        // Sinon, gérer la requête via le framework
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><meta charset='UTF-8'><title>Framework</title></head>");
            out.println("<body>");
            out.println("<h1>FrontServlet a intercepté l'URL : " + path + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}