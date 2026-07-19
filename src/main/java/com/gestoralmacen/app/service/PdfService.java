package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.ReporteInventarioRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteMovimientoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteProductoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteSuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.ReporteResponseDTO;

public interface PdfService {
    ReporteResponseDTO generarInventario(ReporteInventarioRequestDTO request);
    ReporteResponseDTO generarMovimiento(ReporteMovimientoRequestDTO request);
    ReporteResponseDTO generarProducto(ReporteProductoRequestDTO request);
    ReporteResponseDTO generarSuscripcion(ReporteSuscripcionRequestDTO request);
    ReporteResponseDTO generarDashboard(Long empresaId, String userRol);
}
