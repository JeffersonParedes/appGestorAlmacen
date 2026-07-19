package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.response.DashboardAdminResponseDTO;
import com.gestoralmacen.app.dto.response.DashboardBodegueroResponseDTO;

public interface DashboardService {
    DashboardAdminResponseDTO obtenerDashboardAdmin();
    DashboardBodegueroResponseDTO obtenerDashboardBodeguero(Long empresaId);
}
