package com.gestoralmacen.app.controller;

import com.gestoralmacen.app.dto.response.DashboardAdminResponseDTO;
import com.gestoralmacen.app.dto.response.DashboardBodegueroResponseDTO;
import com.gestoralmacen.app.security.SecurityContextHelper;
import com.gestoralmacen.app.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityContextHelper securityHelper;

    public DashboardController(DashboardService dashboardService, SecurityContextHelper securityHelper) {
        this.dashboardService = dashboardService;
        this.securityHelper = securityHelper;
    }

    @GetMapping("/admin")
    public ResponseEntity<DashboardAdminResponseDTO> obtenerDashboardAdmin() {
        return ResponseEntity.ok(dashboardService.obtenerDashboardAdmin());
    }

    @GetMapping("/bodeguero")
    public ResponseEntity<DashboardBodegueroResponseDTO> obtenerDashboardBodeguero() {
        Long empresaId = securityHelper.getEmpresaId();
        return ResponseEntity.ok(dashboardService.obtenerDashboardBodeguero(empresaId));
    }
}
