package com.homeservices.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HttpSession session = request.getSession(false);
        String role = session == null ? null : (String) session.getAttribute(SessionKeys.USER_ROLE);

        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/usuarios/login?requerido=true");
            return false;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        String requiredRole = requiredRole(path);
        if (requiredRole != null && !requiredRole.equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/acceso-denegado");
            return false;
        }
        return true;
    }

    private String requiredRole(String path) {
        if (path.startsWith("/admin")) {
            return "ADMIN";
        }
        if (path.startsWith("/proveedor")) {
            return "PROVEEDOR";
        }
        if (path.startsWith("/cliente")) {
            return "CLIENTE";
        }
        if (path.startsWith("/solicitudes/") && path.endsWith("/estado")) {
            return "PROVEEDOR";
        }
        if (path.startsWith("/solicitudes")) {
            return "CLIENTE";
        }
        return null;
    }
}
