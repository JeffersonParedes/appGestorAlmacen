package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.MovimientoRequestDTO;
import com.gestoralmacen.app.dto.response.MovimientoResponseDTO;
import com.gestoralmacen.app.dto.response.InventarioResponseDTO;

import java.util.List;

public interface MovimientoService {
    // 1. Ver el Kardex (Historial de todos los movimientos de una empresa)
    List<MovimientoResponseDTO> listarHistorialPorEmpresa(Long empresaId);

    // 2. Ver el stock actual en los almacenes
    List<InventarioResponseDTO> listarInventarioPorEmpresa(Long empresaId);

    // 3. Registrar un movimiento (Entrada, Salida, etc.) y actualizar stock
    MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO requestDTO, Long empresaId, Long usuarioId);

    MovimientoResponseDTO registrarEntrada(MovimientoRequestDTO requestDTO, Long empresaId, Long usuarioId);

    MovimientoResponseDTO registrarSalida(MovimientoRequestDTO requestDTO, Long empresaId, Long usuarioId);

    MovimientoResponseDTO registrarTraslado(MovimientoRequestDTO requestDTO, Long empresaId, Long usuarioId);

    MovimientoResponseDTO registrarAjuste(MovimientoRequestDTO requestDTO, Long empresaId, Long usuarioId);
}

