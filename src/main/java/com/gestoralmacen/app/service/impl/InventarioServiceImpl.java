package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.entity.Almacen;
import com.gestoralmacen.app.entity.Inventario;
import com.gestoralmacen.app.entity.Lote;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.mapper.InventarioMapper;
import com.gestoralmacen.app.repository.AlmacenRepository;
import com.gestoralmacen.app.repository.InventarioRepository;
import com.gestoralmacen.app.repository.LoteRepository;
import com.gestoralmacen.app.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final LoteRepository loteRepository;
    private final AlmacenRepository almacenRepository;
    private final InventarioMapper inventarioMapper;

    public InventarioServiceImpl(InventarioRepository inventarioRepository,
                                 LoteRepository loteRepository,
                                 AlmacenRepository almacenRepository,
                                 InventarioMapper inventarioMapper) {
        this.inventarioRepository = inventarioRepository;
        this.loteRepository = loteRepository;
        this.almacenRepository = almacenRepository;
        this.inventarioMapper = inventarioMapper;
    }

    @Override
    @Transactional
    public List<InventarioResponseDTO> listarPorEmpresa(Long empresaId) {
        sincronizarLotesExistentes(empresaId);

        return inventarioRepository.findByEmpresaId(empresaId).stream()
                .filter(i -> i.getAlmacen() != null && !"INACTIVO".equalsIgnoreCase(i.getAlmacen().getEstado()))
                .filter(i -> i.getProducto() != null && !"ELIMINADO".equalsIgnoreCase(i.getProducto().getEstado()))
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<InventarioResponseDTO> listarPorAlmacen(Long almacenId, Long empresaId) {
        sincronizarLotesExistentes(empresaId);

        return inventarioRepository.findByAlmacenId(almacenId).stream()
                .filter(i -> i.getEmpresa().getId().equals(empresaId))
                .filter(i -> i.getAlmacen() != null && !"INACTIVO".equalsIgnoreCase(i.getAlmacen().getEstado()))
                .filter(i -> i.getProducto() != null && !"ELIMINADO".equalsIgnoreCase(i.getProducto().getEstado()))
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerPorProductoYAlmacen(Long productoId, Long almacenId, Long empresaId) {
        Inventario inventario = inventarioRepository.findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, productoId, almacenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró registro de inventario para el producto y almacén especificados."));
        return inventarioMapper.toResponse(inventario);
    }

    private void sincronizarLotesExistentes(Long empresaId) {
        List<Almacen> almacenesActivos = almacenRepository.findByEmpresaId(empresaId).stream()
                .filter(a -> !"INACTIVO".equalsIgnoreCase(a.getEstado()))
                .collect(Collectors.toList());

        if (almacenesActivos.isEmpty()) {
            return;
        }

        Almacen almacenPrincipal = almacenesActivos.get(0);

        List<Lote> lotesActivos = loteRepository.findByEmpresaId(empresaId).stream()
                .filter(l -> l.getCantidadActual() != null && l.getCantidadActual().compareTo(BigDecimal.ZERO) > 0)
                .filter(l -> !"INACTIVO".equalsIgnoreCase(l.getEstado()))
                .collect(Collectors.toList());

        Map<Long, BigDecimal> stockPorProducto = lotesActivos.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getProducto().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Lote::getCantidadActual, BigDecimal::add)
                ));

        for (Map.Entry<Long, BigDecimal> entry : stockPorProducto.entrySet()) {
            Long productoId = entry.getKey();
            BigDecimal totalStockLotes = entry.getValue();

            Lote primerLoteProducto = lotesActivos.stream()
                    .filter(l -> l.getProducto().getId().equals(productoId))
                    .findFirst().orElse(null);

            if (primerLoteProducto != null) {
                Inventario inventario = inventarioRepository
                        .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, productoId, almacenPrincipal.getId())
                        .orElseGet(() -> {
                            Inventario nuevo = new Inventario();
                            nuevo.setEmpresa(almacenPrincipal.getEmpresa());
                            nuevo.setProducto(primerLoteProducto.getProducto());
                            nuevo.setAlmacen(almacenPrincipal);
                            nuevo.setStockActual(BigDecimal.ZERO);
                            nuevo.setStockMinimo(primerLoteProducto.getProducto().getStockMinimo() != null ? primerLoteProducto.getProducto().getStockMinimo() : BigDecimal.ZERO);
                            return nuevo;
                        });

                if (inventario.getStockActual().compareTo(totalStockLotes) != 0) {
                    inventario.setStockActual(totalStockLotes);
                    inventario.setStockMinimo(primerLoteProducto.getProducto().getStockMinimo() != null ? primerLoteProducto.getProducto().getStockMinimo() : BigDecimal.ZERO);
                    inventarioRepository.save(inventario);
                }
            }
        }
    }
}
