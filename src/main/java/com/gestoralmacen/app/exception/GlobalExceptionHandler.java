package com.gestoralmacen.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Atrapa los errores cuando no encontramos algo en la BD (Error 404)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "No Encontrado",
                ex.getMessage(),
                null);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Atrapa los errores de lógica de negocio (Error 400 Bad Request)
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> handleReglaNegocio(ReglaNegocioException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Conflicto de Negocio",
                ex.getMessage(),
                null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 2.1 Atrapa errores de acceso no autorizado (Error 401 Unauthorized)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "No Autorizado",
                ex.getMessage(),
                null);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 2.2 Atrapa errores de acceso prohibido (Error 403 Forbidden)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acceso Denegado",
                ex.getMessage(),
                null);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 3. Atrapa los errores de las validaciones de los DTOs (@NotBlank, @Size,
    // @NotNull)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidaciones(MethodArgumentNotValidException ex) {
        List<String> detallesError = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            detallesError.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación",
                "Los datos enviados no cumplen con los requisitos",
                detallesError);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 4. Atrapa cualquier otro error genérico (Error 500) para que no rompa la
    // aplicación
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ocurrió un error inesperado. Contacte al administrador.",
                null);
        // Opcional: Puedes hacer un System.out.println(ex.getMessage()) aquí para ver
        // en tu consola qué falló exactamente
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
