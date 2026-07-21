package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.LoteRequestDTO;
import com.gestoralmacen.app.dto.response.LoteResponseDTO;
import com.gestoralmacen.app.entity.*;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.LoteMapper;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.service.LoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteServiceImpl implements LoteService {

    private final LoteRepository loteRepository;
    private final LoteMapper loteMapper;
    private final EmpresaRepository empresaRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final InventarioRepository inventarioRepository;
    private final HistorialMovimientosRepository movimientosRepository;
    private final UsuarioRepository usuarioRepository;

    public LoteServiceImpl(LoteRepository loteRepository,
                           LoteMapper loteMapper,
                           EmpresaRepository empresaRepository,
                           ProductoRepository productoRepository,
                           AlmacenRepository almacenRepository,
                           InventarioRepository inventarioRepository,
                           HistorialMovimientosRepository movimientosRepository,
                           UsuarioRepository usuarioRepository) {
        this.loteRepository = loteRepository;
        this.loteMapper = loteMapper;
        this.empresaRepository = empresaRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientosRepository = movimientosRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public LoteResponseDTO crearLote(LoteRequestDTO dto, Long empresaId) {
        return crearLote(dto, empresaId, null);
    }

    @Override
    @Transactional
    public LoteResponseDTO crearLote(LoteRequestDTO dto, Long empresaId, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        if (dto.getProductoId() == null) {
            throw new ReglaNegocioException("Debe seleccionar un producto.");
        }

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto no pertenece a esta empresa.");
        }

        String numLote = dto.getNumeroLote() != null ? dto.getNumeroLote().trim() : "";
        if (numLote.isEmpty()) {
            throw new ReglaNegocioException("El número de lote es obligatorio.");
        }

        if (loteRepository.existsByEmpresaIdAndNumeroLote(empresaId, numLote)) {
            throw new ReglaNegocioException("El número de lote ya se encuentra registrado.");
        }

        if (dto.getFechaVencimiento() != null && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de vencimiento no puede ser menor a la fecha actual.");
        }

        if (dto.getCantidadActual() == null || dto.getCantidadActual().compareTo(BigDecimal.ZERO) <= 0 
                || dto.getCantidadActual().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("La cantidad inicial debe ser un número entero mayor a cero.");
        }

        Lote lote = loteMapper.toEntity(dto);
        lote.setEmpresa(empresa);
        lote.setProducto(producto);
        lote.setNumeroLote(numLote);
        lote.setCostoCompra(producto.getPrecio());

        Lote guardado = loteRepository.save(lote);

        // Determinar almacén de destino (por DTO o primer almacén activo de la empresa)
        Almacen almacenDestino = null;
        if (dto.getAlmacenId() != null) {
            almacenDestino = almacenRepository.findById(dto.getAlmacenId()).orElse(null);
        }
        if (almacenDestino == null) {
            List<Almacen> almacenesEmpresa = almacenRepository.findByEmpresaId(empresaId).stream()
                    .filter(a -> !"INACTIVO".equalsIgnoreCase(a.getEstado()))
                    .collect(Collectors.toList());
            if (!almacenesEmpresa.isEmpty()) {
                almacenDestino = almacenesEmpresa.get(0);
            }
        }

        // Obtener usuario autenticado o primer usuario disponible de la empresa
        Usuario usuarioOperacion = null;
        if (usuarioId != null) {
            usuarioOperacion = usuarioRepository.findById(usuarioId).orElse(null);
        }
        if (usuarioOperacion == null) {
            List<Usuario> usuariosEmpresa = usuarioRepository.findByEmpresaId(empresaId);
            if (!usuariosEmpresa.isEmpty()) {
                usuarioOperacion = usuariosEmpresa.get(0);
            }
        }

        // Actualizar/Crear registro en la tabla 'inventario'
        if (almacenDestino != null) {
            final Almacen targetAlmacen = almacenDestino;
            Inventario inventario = inventarioRepository
                    .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), targetAlmacen.getId())
                    .orElseGet(() -> {
                        Inventario nuevoInv = new Inventario();
                        nuevoInv.setEmpresa(empresa);
                        nuevoInv.setProducto(producto);
                        nuevoInv.setAlmacen(targetAlmacen);
                        nuevoInv.setStockActual(BigDecimal.ZERO);
                        nuevoInv.setStockMinimo(producto.getStockMinimo() != null ? producto.getStockMinimo() : BigDecimal.ZERO);
                        return nuevoInv;
                    });

            inventario.setStockActual(inventario.getStockActual().add(dto.getCantidadActual()));
            inventario.setStockMinimo(producto.getStockMinimo() != null ? producto.getStockMinimo() : BigDecimal.ZERO);
            inventarioRepository.save(inventario);

            // Registrar movimiento de Entrada en Kardex con usuario obligatorio
            if (usuarioOperacion != null) {
                HistorialMovimientos mov = new HistorialMovimientos();
                mov.setEmpresa(empresa);
                mov.setProducto(producto);
                mov.setAlmacen(targetAlmacen);
                mov.setLote(guardado);
                mov.setUsuario(usuarioOperacion);
                mov.setTipoMovimiento("ENTRADA");
                mov.setCantidad(dto.getCantidadActual());
                mov.setMotivo("Registro de lote inicial #" + guardado.getNumeroLote());
                movimientosRepository.save(mov);
            }
        }

        return loteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LoteResponseDTO obtenerLotePorId(Long id, Long empresaId) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado con ID: " + id));

        if (!lote.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este lote.");
        }

        return loteMapper.toResponse(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarLotesPorProducto(Long productoId, Long empresaId) {
        return loteRepository.findByProductoId(productoId).stream()
                .filter(l -> l.getEmpresa().getId().equals(empresaId))
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> consultarLotesPorVencer(Long empresaId, int diasThreshold) {
        LocalDate limitDate = LocalDate.now().plusDays(diasThreshold);
        return loteRepository.findByEmpresaId(empresaId).stream()
                .filter(l -> l.getFechaVencimiento() != null && 
                             !l.getFechaVencimiento().isBefore(LocalDate.now()) && 
                             l.getFechaVencimiento().isBefore(limitDate))
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
