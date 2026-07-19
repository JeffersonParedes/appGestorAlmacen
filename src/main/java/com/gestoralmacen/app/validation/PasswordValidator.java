package com.gestoralmacen.app.validation;

import com.gestoralmacen.app.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    public void validar(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ReglaNegocioException("La contraseña no puede estar vacía.");
        }
        if (password.length() < 6) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 6 caracteres.");
        }
    }
}
