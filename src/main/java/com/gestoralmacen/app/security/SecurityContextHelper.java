package com.gestoralmacen.app.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {

    private final JwtUtil jwtUtil;

    public SecurityContextHelper(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Método para obtener el ID de la empresa del Token actual
    public Long getEmpresaId() {
        String token = getTokenFromContext();
        Claims claims = jwtUtil.extraerClaims(token);
        return claims.get("empresaId", Long.class);
    }

    // Método para obtener el ID del usuario del Token actual
    public Long getUsuarioId() {
        String token = getTokenFromContext();
        Claims claims = jwtUtil.extraerClaims(token);
        return claims.get("usuarioId", Long.class);
    }

    // Método para obtener el Rol del usuario del Token actual
    public String getUserRol() {
        String token = getTokenFromContext();
        Claims claims = jwtUtil.extraerClaims(token);
        return claims.get("rol", String.class);
    }

    private String getTokenFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            return auth.getCredentials().toString(); // ¡Aquí recuperamos el JWT que guardamos!
        }
        throw new RuntimeException("No hay sesión activa");
    }
}