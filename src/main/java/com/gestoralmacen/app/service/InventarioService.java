package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import java.util.List;

public interface InventarioService {
    List<InventarioResponseDTO> listarPorEmpresa(Long empresaId);
    List<InventarioResponseDTO> listarPorAlmacen(Long almacenId, Long empresaId);
    InventarioResponseDTO obtenerPorProductoYAlmacen(Long productoId, Long almacenId, Long empresaId);
}
