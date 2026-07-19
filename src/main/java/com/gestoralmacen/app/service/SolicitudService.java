package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.SolicitudRequestDTO;
import com.gestoralmacen.app.dto.response.SolicitudResponseDTO;
import java.util.List;

public interface SolicitudService {
    SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO dto, Long empresaId, Long usuarioId);
    SolicitudResponseDTO obtenerPorId(Long id, Long empresaId);
    List<SolicitudResponseDTO> listarPorEmpresa(Long empresaId);
    void procesarSolicitud(Long id, String estado, String motivoRechazo, Long empresaId);
}
