package com.gestoralmacen.app.notification;

import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import com.gestoralmacen.app.entity.Notificacion;
import java.util.List;

public interface NotificationService {
    void enviarYGuardar(Notificacion notificacion);
    List<NotificacionResponseDTO> listarActivasPorUsuario(Long usuarioId);
    void marcarComoLeida(Long notificacionId);
}
