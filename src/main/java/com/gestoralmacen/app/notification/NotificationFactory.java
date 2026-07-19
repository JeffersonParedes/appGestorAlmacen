package com.gestoralmacen.app.notification;

import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.entity.Usuario;

public class NotificationFactory {

    public static Notificacion crearSuscripcionVencida(Empresa empresa, Usuario usuario, String mensaje) {
        Notificacion n = new Notificacion();
        n.setEmpresa(empresa);
        n.setUsuario(usuario);
        n.setTipo("SUSCRIPCION");
        n.setTitulo("Suscripción Vencida");
        n.setMensaje(mensaje);
        n.setLeido(false);
        return n;
    }

    public static Notificacion crearStockMinimo(Empresa empresa, Usuario usuario, String mensaje) {
        Notificacion n = new Notificacion();
        n.setEmpresa(empresa);
        n.setUsuario(usuario);
        n.setTipo("STOCK");
        n.setTitulo("Stock Mínimo Superado");
        n.setMensaje(mensaje);
        n.setLeido(false);
        return n;
    }

    public static Notificacion crearProductoProximoVencer(Empresa empresa, Usuario usuario, String mensaje) {
        Notificacion n = new Notificacion();
        n.setEmpresa(empresa);
        n.setUsuario(usuario);
        n.setTipo("VENCIMIENTO");
        n.setTitulo("Producto Próximo a Vencer");
        n.setMensaje(mensaje);
        n.setLeido(false);
        return n;
    }

    public static Notificacion crearSolicitudAprobacion(Empresa empresa, Usuario usuario, String mensaje) {
        Notificacion n = new Notificacion();
        n.setEmpresa(empresa);
        n.setUsuario(usuario);
        n.setTipo("APROBACION");
        n.setTitulo("Nueva Solicitud de Aprobación");
        n.setMensaje(mensaje);
        n.setLeido(false);
        return n;
    }
}
