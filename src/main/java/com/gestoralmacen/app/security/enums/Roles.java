package com.gestoralmacen.app.security.enums;

public enum Roles {
    ADMINISTRADOR,
    BODEGUERO,
    EMPLEADO;

    public String getRoleName() {
        return name();
    }
}
