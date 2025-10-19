package com.projet_framework;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet{


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
             resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().write("Voici l'URL recu : "+resourcePath);
            return;
        }

    }
