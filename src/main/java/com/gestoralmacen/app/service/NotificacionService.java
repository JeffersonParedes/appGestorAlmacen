package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    NotificacionResponseDTO registrarNotificacion(NotificacionRequestDTO dto);
    void marcarComoLeida(Long id);
    List<NotificacionResponseDTO> consultarPorUsuario(Long usuarioId);
    List<NotificacionResponseDTO> consultarPorAdministrador(Long adminId);
}
