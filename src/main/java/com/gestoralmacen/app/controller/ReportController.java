package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.request.ReporteInventarioRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteMovimientoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteProductoRequestDTO;
import com.gestoralmacen.app.dto.request.ReporteSuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.ReporteResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.PdfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReportController {

    private final PdfService pdfService;
    private final SecurityContextHelper securityHelper;

    public ReportController(PdfService pdfService, SecurityContextHelper securityHelper) {
        this.pdfService = pdfService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/inventario")
    public ResponseEntity<ReporteResponseDTO> generarInventario(ReporteInventarioRequestDTO request) {
        String rol = securityHelper.getUserRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            request.setEmpresaId(securityHelper.getEmpresaId());
        }
        return ResponseEntity.ok(pdfService.generarInventario(request));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<ReporteResponseDTO> generarMovimiento(ReporteMovimientoRequestDTO request) {
        String rol = securityHelper.getUserRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            request.setEmpresaId(securityHelper.getEmpresaId());
        }
        return ResponseEntity.ok(pdfService.generarMovimiento(request));
    }

    @GetMapping("/productos")
    public ResponseEntity<ReporteResponseDTO> generarProducto(ReporteProductoRequestDTO request) {
        String rol = securityHelper.getUserRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            request.setEmpresaId(securityHelper.getEmpresaId());
        }
        return ResponseEntity.ok(pdfService.generarProducto(request));
    }

    @GetMapping("/suscripciones")
    public ResponseEntity<ReporteResponseDTO> generarSuscripcion(ReporteSuscripcionRequestDTO request) {
        String rol = securityHelper.getUserRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            request.setEmpresaId(securityHelper.getEmpresaId());
        }
        return ResponseEntity.ok(pdfService.generarSuscripcion(request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ReporteResponseDTO> generarDashboard() {
        Long empresaId = null;
        String rol = securityHelper.getUserRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            empresaId = securityHelper.getEmpresaId();
        }
        return ResponseEntity.ok(pdfService.generarDashboard(empresaId, rol));
    }
}
