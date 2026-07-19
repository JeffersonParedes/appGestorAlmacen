package com.gestoralmacen.app.validation;

import com.gestoralmacen.app.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProductoValidator {

    public void validar(String nombre, BigDecimal precio, BigDecimal stockMinimo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del producto no puede estar vacío.");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El precio de referencia no puede ser negativo.");
        }
        if (stockMinimo == null || stockMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo.");
        }
    }
}
