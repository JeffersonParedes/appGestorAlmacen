package com.gestoralmacen.app.validation;

import com.gestoralmacen.app.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;

@Component
public class UsuarioValidator {

    public void validar(String usuario, String correo, String dni) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de usuario es obligatorio.");
        }
        if (usuario.length() < 3) {
            throw new ReglaNegocioException("El nombre de usuario debe tener al menos 3 caracteres.");
        }
        if (correo == null || !correo.contains("@")) {
            throw new ReglaNegocioException("El formato del correo electrónico es inválido.");
        }
        if (dni != null && (dni.length() < 8 || dni.length() > 20)) {
            throw new ReglaNegocioException("El DNI debe tener entre 8 y 20 caracteres.");
        }
    }
}
