package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.SuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import java.util.List;

public interface SuscripcionService {
    SuscripcionResponseDTO registrarSuscripcion(SuscripcionRequestDTO dto);
    SuscripcionResponseDTO renovarSuscripcion(Long empresaId, SuscripcionRequestDTO dto);
    List<SuscripcionResponseDTO> consultarSuscripcionesPorVencer(int diasThreshold);
    List<SuscripcionResponseDTO> consultarHistorial(Long empresaId);
    void actualizarEstadoSuscripciones();
}
