package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.ReporteInventarioRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteMovimientoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteProductoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteSuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.DashboardAdminResponseDTO;
import com.gestoralmacen.app.dto.response.DashboardBodegueroResponseDTO;
import com.gestoralmacen.app.dto.response.ReporteResponseDTO;
import com.gestoralmacen.app.entity.*;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.report.generators.*;
import com.gestoralmacen.app.service.DashboardService;
import com.gestoralmacen.app.service.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfServiceImpl implements PdfService {

    private final EmpresaRepository empresaRepository;
    private final InventarioRepository inventarioRepository;
    private final HistorialMovimientosRepository historialMovimientosRepository;
    private final ProductoRepository productoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final DashboardService dashboardService;

    public PdfServiceImpl(EmpresaRepository empresaRepository,
                          InventarioRepository inventarioRepository,
                          HistorialMovimientosRepository historialMovimientosRepository,
                          ProductoRepository productoRepository,
                          SuscripcionRepository suscripcionRepository,
                          DashboardService dashboardService) {
        this.empresaRepository = empresaRepository;
        this.inventarioRepository = inventarioRepository;
        this.historialMovimientosRepository = historialMovimientosRepository;
        this.productoRepository = productoRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.dashboardService = dashboardService;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarInventario(ReporteInventarioRequestDTO request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<Inventario> inventarios = inventarioRepository.findByEmpresaId(request.getEmpresaId());

        // Filtrar por almacén si es provisto
        if (request.getAlmacenId() != null) {
            inventarios = inventarios.stream()
                    .filter(inv -> inv.getAlmacen().getId().equals(request.getAlmacenId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por stock mínimo
        if (Boolean.TRUE.equals(request.getIncluirStockMinimo())) {
            inventarios = inventarios.stream()
                    .filter(inv -> inv.getStockActual().compareTo(inv.getStockMinimo()) <= 0)
                    .collect(Collectors.toList());
        }

        byte[] pdfBytes = InventarioGenerator.generarPDF(inventarios, empresa.getRazonSocial());
        return new ReporteResponseDTO(
                "reporte_inventario.pdf",
                "application/pdf",
                LocalDateTime.now(),
                (long) pdfBytes.length,
                pdfBytes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarMovimiento(ReporteMovimientoRequestDTO request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<HistorialMovimientos> movimientos = historialMovimientosRepository.findByEmpresaId(request.getEmpresaId());

        // Filtrar por usuario
        if (request.getUsuarioId() != null) {
            movimientos = movimientos.stream()
                    .filter(mov -> mov.getUsuario().getId().equals(request.getUsuarioId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por tipo de movimiento
        if (request.getTipoMovimiento() != null && !request.getTipoMovimiento().isEmpty()) {
            movimientos = movimientos.stream()
                    .filter(mov -> mov.getTipoMovimiento().equalsIgnoreCase(request.getTipoMovimiento()))
                    .collect(Collectors.toList());
        }

        // Filtrar por rango de fechas
        if (request.getFechaInicio() != null) {
            movimientos = movimientos.stream()
                    .filter(mov -> mov.getFechaMovimiento() != null && 
                                  !mov.getFechaMovimiento().toLocalDate().isBefore(request.getFechaInicio()))
                    .collect(Collectors.toList());
        }
        if (request.getFechaFin() != null) {
            movimientos = movimientos.stream()
                    .filter(mov -> mov.getFechaMovimiento() != null && 
                                  !mov.getFechaMovimiento().toLocalDate().isAfter(request.getFechaFin()))
                    .collect(Collectors.toList());
        }

        byte[] pdfBytes = MovimientoGenerator.generarPDF(movimientos, empresa.getRazonSocial());
        return new ReporteResponseDTO(
                "reporte_movimientos.pdf",
                "application/pdf",
                LocalDateTime.now(),
                (long) pdfBytes.length,
                pdfBytes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarProducto(ReporteProductoRequestDTO request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<Producto> productos = productoRepository.findByEmpresaId(request.getEmpresaId());

        // Filtrar por categoría
        if (request.getCategoriaId() != null) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().getId().equals(request.getCategoriaId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por estado
        if (request.getEstadoProducto() != null && !request.getEstadoProducto().isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getEstado().equalsIgnoreCase(request.getEstadoProducto()))
                    .collect(Collectors.toList());
        }

        byte[] pdfBytes = ProductoGenerator.generarPDF(productos, empresa.getRazonSocial());
        return new ReporteResponseDTO(
                "reporte_productos.pdf",
                "application/pdf",
                LocalDateTime.now(),
                (long) pdfBytes.length,
                pdfBytes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarSuscripcion(ReporteSuscripcionRequestDTO request) {
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();

        // Filtrar por empresa
        if (request.getEmpresaId() != null) {
            suscripciones = suscripciones.stream()
                    .filter(s -> s.getEmpresa() != null && s.getEmpresa().getId().equals(request.getEmpresaId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por estado
        if (request.getEstadoSuscripcion() != null && !request.getEstadoSuscripcion().isEmpty()) {
            suscripciones = suscripciones.stream()
                    .filter(s -> s.getEstadoPago().equalsIgnoreCase(request.getEstadoSuscripcion()))
                    .collect(Collectors.toList());
        }

        // Filtrar por rango de fechas
        if (request.getFechaInicio() != null) {
            suscripciones = suscripciones.stream()
                    .filter(s -> s.getFechaInicio() != null && !s.getFechaInicio().isBefore(request.getFechaInicio()))
                    .collect(Collectors.toList());
        }
        if (request.getFechaFin() != null) {
            suscripciones = suscripciones.stream()
                    .filter(s -> s.getFechaFin() != null && !s.getFechaFin().isAfter(request.getFechaFin()))
                    .collect(Collectors.toList());
        }

        byte[] pdfBytes = SuscripcionGenerator.generarPDF(suscripciones);
        return new ReporteResponseDTO(
                "reporte_suscripciones.pdf",
                "application/pdf",
                LocalDateTime.now(),
                (long) pdfBytes.length,
                pdfBytes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponseDTO generarDashboard(Long empresaId, String userRol) {
        Map<String, String> metrics = new LinkedHashMap<>();
        String titulo;

        if ("ADMINISTRADOR".equalsIgnoreCase(userRol)) {
            titulo = "Dashboard Global de Administración SaaS";
            DashboardAdminResponseDTO adminData = dashboardService.obtenerDashboardAdmin();
            metrics.put("Empresas Activas", adminData.getEmpresasActivas().toString());
            metrics.put("Empresas Suspendidas", adminData.getEmpresasSuspendidas().toString());
            metrics.put("Suscripciones Próximas a Vencer", adminData.getSuscripcionesPorVencer().toString());
            metrics.put("Nuevas Empresas Registradas este Mes", adminData.getNuevasEmpresasMes().toString());
        } else {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
            titulo = "Resumen Ejecutivo de Negocio - " + empresa.getRazonSocial();
            DashboardBodegueroResponseDTO bodegueroData = dashboardService.obtenerDashboardBodeguero(empresaId);
            metrics.put("Valor Monetario del Inventario Total", String.format("%.2f", bodegueroData.getInventarioTotal()));
            metrics.put("Catálogo de Productos Registrados", bodegueroData.getProductos().toString());
            metrics.put("Productos con Alerta de Stock Mínimo", bodegueroData.getStockMinimo().toString());
            metrics.put("Lotes Próximos a Vencer (30 días)", bodegueroData.getProductosVencer().toString());
            metrics.put("Solicitudes de Aprobación Pendientes", bodegueroData.getSolicitudes().toString());
        }

        byte[] pdfBytes = DashboardGenerator.generarPDF(titulo, metrics);
        return new ReporteResponseDTO(
                "reporte_dashboard.pdf",
                "application/pdf",
                LocalDateTime.now(),
                (long) pdfBytes.length,
                pdfBytes
        );
    }
}
