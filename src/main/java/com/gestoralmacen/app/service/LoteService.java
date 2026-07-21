package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.LoteRequestDTO;
import com.gestoralmacen.app.dto.response.LoteResponseDTO;
import java.util.List;

public interface LoteService {
    LoteResponseDTO crearLote(LoteRequestDTO dto, Long empresaId);
    LoteResponseDTO crearLote(LoteRequestDTO dto, Long empresaId, Long usuarioId);
    LoteResponseDTO obtenerLotePorId(Long id, Long empresaId);
    List<LoteResponseDTO> listarLotesPorProducto(Long productoId, Long empresaId);
    List<LoteResponseDTO> consultarLotesPorVencer(Long empresaId, int diasThreshold);
}
