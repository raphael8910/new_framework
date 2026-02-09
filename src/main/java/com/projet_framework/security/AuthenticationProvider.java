package com.projet_framework.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthenticationProvider {
    private static final String USER_SESSION_KEY = "AUTHENTICATED_USER";

    public void authenticate(HttpServletRequest req, BaseUser user) {
        HttpSession session = req.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);
        System.out.println("Utilisateur " + user.getUsername() + " authentifié et stocké dans la session.");
    }

    public BaseUser getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (BaseUser) session.getAttribute(USER_SESSION_KEY);
    }

    public void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidée - utilisateur déconnecté.");
        }
    }
}

