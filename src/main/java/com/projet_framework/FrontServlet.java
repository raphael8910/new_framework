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

        String path = req.getPathInfo();

        if(path==null){
            path = "/";
        }
        System.out.println("URL :"+ path );

        resp.getWriter().write("Voici l'URL recu : "+path);
    }
}
