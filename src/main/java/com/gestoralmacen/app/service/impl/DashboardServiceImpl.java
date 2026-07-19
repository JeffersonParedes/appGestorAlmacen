package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.response.DashboardAdminResponseDTO;
import com.gestoralmacen.app.dto.response.DashboardBodegueroResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Inventario;
import com.gestoralmacen.app.entity.Lote;
import com.gestoralmacen.app.entity.Suscripcion;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmpresaRepository empresaRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final LoteRepository loteRepository;
    private final SolicitudRepository solicitudRepository;

    public DashboardServiceImpl(EmpresaRepository empresaRepository,
                                SuscripcionRepository suscripcionRepository,
                                ProductoRepository productoRepository,
                                InventarioRepository inventarioRepository,
                                LoteRepository loteRepository,
                                SolicitudRepository solicitudRepository) {
        this.empresaRepository = empresaRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.loteRepository = loteRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardAdminResponseDTO obtenerDashboardAdmin() {
        List<Empresa> empresas = empresaRepository.findAll();
        long activas = empresas.stream().filter(e -> "ACTIVO".equalsIgnoreCase(e.getEstado())).count();
        long suspendidas = empresas.stream().filter(e -> "SUSPENDIDO".equalsIgnoreCase(e.getEstado())).count();

        LocalDate now = LocalDate.now();
        LocalDate limit = now.plusDays(7);
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        long porVencer = suscripciones.stream()
                .filter(s -> "PAGADO".equalsIgnoreCase(s.getEstadoPago()) && 
                             s.getFechaFin() != null && 
                             !s.getFechaFin().isBefore(now) && 
                             s.getFechaFin().isBefore(limit))
                .count();

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        long nuevasEmpresas = empresas.stream()
                .filter(e -> e.getCreatedAt() != null && 
                             !e.getCreatedAt().toLocalDate().isBefore(firstDayOfMonth))
                .count();

        return new DashboardAdminResponseDTO(activas, suspendidas, porVencer, nuevasEmpresas);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardBodegueroResponseDTO obtenerDashboardBodeguero(Long empresaId) {
        List<Inventario> inventarios = inventarioRepository.findByEmpresaId(empresaId);
        
        double inventarioTotal = inventarios.stream()
                .mapToDouble(inv -> {
                    BigDecimal precio = (inv.getProducto().getPrecio() != null) ? inv.getProducto().getPrecio() : BigDecimal.ZERO;
                    BigDecimal stock = (inv.getStockActual() != null) ? inv.getStockActual() : BigDecimal.ZERO;
                    return stock.multiply(precio).doubleValue();
                })
                .sum();

        long productos = productoRepository.findByEmpresaId(empresaId).stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstadoAprobacion()))
                .count();

        long stockMinimo = inventarios.stream()
                .filter(inv -> inv.getStockActual().compareTo(inv.getStockMinimo()) <= 0)
                .count();

        LocalDate limitDate = LocalDate.now().plusDays(30);
        List<Lote> lotes = loteRepository.findByEmpresaId(empresaId);
        long productosVencer = lotes.stream()
                .filter(l -> l.getFechaVencimiento() != null && 
                             !l.getFechaVencimiento().isBefore(LocalDate.now()) && 
                             l.getFechaVencimiento().isBefore(limitDate))
                .count();

        long solicitudes = solicitudRepository.findByEmpresaId(empresaId).stream()
                .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado()))
                .count();

        return new DashboardBodegueroResponseDTO(inventarioTotal, productos, stockMinimo, productosVencer, solicitudes);
    }
}
