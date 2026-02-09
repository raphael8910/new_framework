package com.projet_framework.security;

import java.lang.reflect.Method;

import com.projet_framework.annotation.security.Authorized;
import com.projet_framework.annotation.security.HasRole;
import com.projet_framework.exception.ForbiddenException;
import com.projet_framework.exception.UnauthorizedException;

public class AuthorizationManager {

    public static void checkAccess(BaseUser user, Method method) throws UnauthorizedException, ForbiddenException {
        // Vérifier si @Authorized est présent
        if (method.isAnnotationPresent(Authorized.class)) {
            if (user == null || !user.isAuthenticated()) {
                throw new UnauthorizedException("Vous devez être connecté pour accéder à cette ressource");
            }
        }

        // Vérifier si @HasRole est présent
        if (method.isAnnotationPresent(HasRole.class)) {
            if (user == null || !user.isAuthenticated()) {
                throw new UnauthorizedException("Vous devez être connecté pour accéder à cette ressource");
            }

            String requiredRole = method.getAnnotation(HasRole.class).value();
            if (!requiredRole.equals(user.getRole())) {
                throw new ForbiddenException("Rôle requis: " + requiredRole + ", votre rôle: " + user.getRole());
            }
        }
    }
}

