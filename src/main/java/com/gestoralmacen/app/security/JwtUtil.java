package com.gestoralmacen.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Esta es tu llave secreta (en producción debería ir en un archivo
    // application.properties)
    // Keys.secretKeyFor genera una llave criptográficamente segura de 256 bits para
    // HS256
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tiempo de expiración del token: 10 horas (en milisegundos)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // 1. Método para generar el token
    public String generarToken(String username, Long usuarioId, Long empresaId, String rol) {
        return Jwts.builder()
                .setSubject(username)
                .claim("usuarioId", usuarioId)
                .claim("empresaId", empresaId)
                .claim("rol", rol)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 2. Extraer todos los datos (Claims) del token
    public Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 3. Obtener solo el nombre de usuario
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    // 4. Validar si el token no ha expirado y pertenece al usuario
    public boolean validarToken(String token, String username) {
        final String tokenUsername = extraerUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpirado(token));
    }

    private boolean isTokenExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }
}
