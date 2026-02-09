package com.projet_framework.security;

public interface BaseUser {
    boolean isAuthenticated();

    String getRole();

    String getUsername();
}

