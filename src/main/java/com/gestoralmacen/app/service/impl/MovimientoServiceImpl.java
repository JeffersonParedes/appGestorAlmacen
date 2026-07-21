package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.MovimientoRequestDTO;
import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.dto.response.MovimientoResponseDTO;
import com.gestoralmacen.app.entity.*;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.InventarioMapper;
import com.gestoralmacen.app.mapper.MovimientoMapper;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.service.MovimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final HistorialMovimientosRepository movimientosRepository;
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final LoteRepository loteRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final MovimientoMapper movimientoMapper;
    private final InventarioMapper inventarioMapper;

    public MovimientoServiceImpl(HistorialMovimientosRepository movimientosRepository,
            InventarioRepository inventarioRepository,
            ProductoRepository productoRepository,
            AlmacenRepository almacenRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            LoteRepository loteRepository,
            AuditoriaRepository auditoriaRepository,
            MovimientoMapper movimientoMapper,
            InventarioMapper inventarioMapper) {
        this.movimientosRepository = movimientosRepository;
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.loteRepository = loteRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.movimientoMapper = movimientoMapper;
        this.inventarioMapper = inventarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> listarHistorialPorEmpresa(Long empresaId) {
        return movimientosRepository.findByEmpresaId(empresaId).stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarInventarioPorEmpresa(Long empresaId) {
        return inventarioRepository.findByEmpresaId(empresaId).stream()
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO dto, Long empresaId, Long usuarioId) {
        String tipo = dto.getTipoMovimiento() != null ? dto.getTipoMovimiento().toUpperCase() : "";
        switch (tipo) {
            case "ENTRADA":
            case "DEVOLUCION_CLIENTE":
                return registrarEntrada(dto, empresaId, usuarioId);
            case "SALIDA":
                return registrarSalida(dto, empresaId, usuarioId);
            case "TRASLADO":
                return registrarTraslado(dto, empresaId, usuarioId);
            case "AJUSTE":
                return registrarAjuste(dto, empresaId, usuarioId);
            default:
                throw new ReglaNegocioException("Tipo de movimiento no soportado: " + tipo);
        }
    }

    @Override
    @Transactional
    public MovimientoResponseDTO registrarEntrada(MovimientoRequestDTO dto, Long empresaId, Long usuarioId) {
        validarCantidadEnteraPositiva(dto.getCantidad());

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId) || !almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto o almacén no pertenecen a esta empresa.");
        }
        if (!"APROBADO".equalsIgnoreCase(producto.getEstadoAprobacion())) {
            throw new ReglaNegocioException("El producto no está aprobado.");
        }

        Inventario inventario = inventarioRepository
                .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), almacen.getId())
                .orElseGet(() -> {
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setEmpresa(empresa);
                    nuevoInventario.setProducto(producto);
                    nuevoInventario.setAlmacen(almacen);
                    nuevoInventario.setStockActual(BigDecimal.ZERO);
                    return nuevoInventario;
                });
        inventario.setStockActual(inventario.getStockActual().add(dto.getCantidad()));
        inventarioRepository.save(inventario);

        Lote lote = null;
        if (dto.getLoteId() != null) {
            lote = loteRepository.findById(dto.getLoteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado"));
            if (!lote.getProducto().getId().equals(producto.getId())) {
                throw new ReglaNegocioException("El lote no pertenece a este producto.");
            }
            lote.setCantidadActual(lote.getCantidadActual().add(dto.getCantidad()));
            loteRepository.save(lote);
        }

        HistorialMovimientos movimiento = movimientoMapper.toEntity(dto);
        movimiento.setEmpresa(empresa);
        movimiento.setUsuario(usuario);
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("ENTRADA");
        HistorialMovimientos guardado = movimientosRepository.save(movimiento);

        Auditoria auditoria = new Auditoria();
        auditoria.setEmpresa(empresa);
        auditoria.setUsuario(usuario);
        auditoria.setAccion("REGISTRAR_ENTRADA");
        auditoria.setTablaAfectada("historial_movimientos");
        auditoria.setRegistroId(guardado.getId());
        auditoria.setDescripcion("Entrada registrada para producto: " + producto.getNombre() + " en almacén: " + almacen.getNombre() + ", cantidad: " + dto.getCantidad());
        auditoriaRepository.save(auditoria);

        return movimientoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO registrarSalida(MovimientoRequestDTO dto, Long empresaId, Long usuarioId) {
        validarCantidadEnteraPositiva(dto.getCantidad());

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId) || !almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto o almacén no pertenecen a esta empresa.");
        }
        if (!"APROBADO".equalsIgnoreCase(producto.getEstadoAprobacion())) {
            throw new ReglaNegocioException("El producto no está aprobado.");
        }

        Inventario inventario = inventarioRepository
                .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), almacen.getId())
                .orElseThrow(() -> new ReglaNegocioException("No existe suficiente stock disponible."));

        if (inventario.getStockActual().compareTo(dto.getCantidad()) < 0) {
            throw new ReglaNegocioException("No existe suficiente stock disponible.");
        }

        Lote lote = null;
        if (dto.getLoteId() != null) {
            lote = loteRepository.findById(dto.getLoteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado"));
            if (!lote.getProducto().getId().equals(producto.getId())) {
                throw new ReglaNegocioException("El lote no pertenece a este producto.");
            }
            if (lote.getCantidadActual().compareTo(dto.getCantidad()) < 0) {
                throw new ReglaNegocioException("No existe suficiente stock disponible.");
            }
            lote.setCantidadActual(lote.getCantidadActual().subtract(dto.getCantidad()));
            loteRepository.save(lote);
        }

        inventario.setStockActual(inventario.getStockActual().subtract(dto.getCantidad()));
        inventarioRepository.save(inventario);

        HistorialMovimientos movimiento = movimientoMapper.toEntity(dto);
        movimiento.setEmpresa(empresa);
        movimiento.setUsuario(usuario);
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("SALIDA");
        HistorialMovimientos guardado = movimientosRepository.save(movimiento);

        Auditoria auditoria = new Auditoria();
        auditoria.setEmpresa(empresa);
        auditoria.setUsuario(usuario);
        auditoria.setAccion("REGISTRAR_SALIDA");
        auditoria.setTablaAfectada("historial_movimientos");
        auditoria.setRegistroId(guardado.getId());
        auditoria.setDescripcion("Salida registrada para producto: " + producto.getNombre() + " en almacén: " + almacen.getNombre() + ", cantidad: " + dto.getCantidad());
        auditoriaRepository.save(auditoria);

        return movimientoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO registrarTraslado(MovimientoRequestDTO dto, Long empresaId, Long usuarioId) {
        validarCantidadEnteraPositiva(dto.getCantidad());

        if (dto.getDestinoAlmacenId() == null) {
            throw new ReglaNegocioException("El almacén de destino es obligatorio para traslados.");
        }
        if (dto.getAlmacenId().equals(dto.getDestinoAlmacenId())) {
            throw new ReglaNegocioException("El almacén de origen y destino no pueden ser el mismo.");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        Almacen almacenOrigen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén de origen no encontrado"));
        Almacen almacenDestino = almacenRepository.findById(dto.getDestinoAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén de destino no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId) || 
            !almacenOrigen.getEmpresa().getId().equals(empresaId) || 
            !almacenDestino.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto o almacenes no pertenecen a esta empresa.");
        }

        Inventario invOrigen = inventarioRepository
                .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), almacenOrigen.getId())
                .orElseThrow(() -> new ReglaNegocioException("No existe suficiente stock disponible."));

        if (invOrigen.getStockActual().compareTo(dto.getCantidad()) < 0) {
            throw new ReglaNegocioException("No existe suficiente stock disponible.");
        }

        Lote lote = null;
        if (dto.getLoteId() != null) {
            lote = loteRepository.findById(dto.getLoteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado"));
            if (!lote.getProducto().getId().equals(producto.getId())) {
                throw new ReglaNegocioException("El lote no pertenece a este producto.");
            }
            if (lote.getCantidadActual().compareTo(dto.getCantidad()) < 0) {
                throw new ReglaNegocioException("No existe suficiente stock disponible.");
            }
        }

        invOrigen.setStockActual(invOrigen.getStockActual().subtract(dto.getCantidad()));
        inventarioRepository.save(invOrigen);

        Inventario invDestino = inventarioRepository
                .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), almacenDestino.getId())
                .orElseGet(() -> {
                    Inventario nuevoInv = new Inventario();
                    nuevoInv.setEmpresa(empresa);
                    nuevoInv.setProducto(producto);
                    nuevoInv.setAlmacen(almacenDestino);
                    nuevoInv.setStockActual(BigDecimal.ZERO);
                    return nuevoInv;
                });
        invDestino.setStockActual(invDestino.getStockActual().add(dto.getCantidad()));
        inventarioRepository.save(invDestino);

        HistorialMovimientos movOrigen = new HistorialMovimientos();
        movOrigen.setEmpresa(empresa);
        movOrigen.setUsuario(usuario);
        movOrigen.setProducto(producto);
        movOrigen.setAlmacen(almacenOrigen);
        movOrigen.setLote(lote);
        movOrigen.setTipoMovimiento("TRASLADO");
        movOrigen.setCantidad(dto.getCantidad());
        movOrigen.setMotivo("Traslado hacia almacén: " + almacenDestino.getNombre() + ". " + (dto.getMotivo() != null ? dto.getMotivo() : ""));
        movimientosRepository.save(movOrigen);

        HistorialMovimientos movDestino = new HistorialMovimientos();
        movDestino.setEmpresa(empresa);
        movDestino.setUsuario(usuario);
        movDestino.setProducto(producto);
        movDestino.setAlmacen(almacenDestino);
        movDestino.setLote(lote);
        movDestino.setTipoMovimiento("TRASLADO");
        movDestino.setCantidad(dto.getCantidad());
        movDestino.setMotivo("Traslado desde almacén: " + almacenOrigen.getNombre() + ". " + (dto.getMotivo() != null ? dto.getMotivo() : ""));
        HistorialMovimientos guardadoDestino = movimientosRepository.save(movDestino);

        Auditoria auditoria = new Auditoria();
        auditoria.setEmpresa(empresa);
        auditoria.setUsuario(usuario);
        auditoria.setAccion("REGISTRAR_TRASLADO");
        auditoria.setTablaAfectada("historial_movimientos");
        auditoria.setRegistroId(guardadoDestino.getId());
        auditoria.setDescripcion("Traslado de producto: " + producto.getNombre() + " desde almacén " + almacenOrigen.getNombre() + " a " + almacenDestino.getNombre() + ", cantidad: " + dto.getCantidad());
        auditoriaRepository.save(auditoria);

        return movimientoMapper.toResponse(guardadoDestino);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO registrarAjuste(MovimientoRequestDTO dto, Long empresaId, Long usuarioId) {
        if (dto.getCantidad() == null || dto.getCantidad().compareTo(BigDecimal.ZERO) < 0 || dto.getCantidad().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("La cantidad sólo permite números enteros.");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId) || !almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto o almacén no pertenecen a esta empresa.");
        }

        Inventario inventario = inventarioRepository
                .findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, producto.getId(), almacen.getId())
                .orElseGet(() -> {
                    Inventario nuevoInv = new Inventario();
                    nuevoInv.setEmpresa(empresa);
                    nuevoInv.setProducto(producto);
                    nuevoInv.setAlmacen(almacen);
                    nuevoInv.setStockActual(BigDecimal.ZERO);
                    return nuevoInv;
                });

        BigDecimal stockAnterior = inventario.getStockActual();
        BigDecimal nuevoStock = dto.getCantidad();
        inventario.setStockActual(nuevoStock);
        inventarioRepository.save(inventario);

        Lote lote = null;
        if (dto.getLoteId() != null) {
            lote = loteRepository.findById(dto.getLoteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado"));
            if (!lote.getProducto().getId().equals(producto.getId())) {
                throw new ReglaNegocioException("El lote no pertenece a este producto.");
            }
            lote.setCantidadActual(nuevoStock);
            loteRepository.save(lote);
        }

        HistorialMovimientos movimiento = movimientoMapper.toEntity(dto);
        movimiento.setEmpresa(empresa);
        movimiento.setUsuario(usuario);
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("AJUSTE");
        movimiento.setMotivo("Ajuste de inventario. Stock anterior: " + stockAnterior + ", Nuevo stock: " + nuevoStock + ". " + (dto.getMotivo() != null ? dto.getMotivo() : ""));
        HistorialMovimientos guardado = movimientosRepository.save(movimiento);

        Auditoria auditoria = new Auditoria();
        auditoria.setEmpresa(empresa);
        auditoria.setUsuario(usuario);
        auditoria.setAccion("REGISTRAR_AJUSTE");
        auditoria.setTablaAfectada("historial_movimientos");
        auditoria.setRegistroId(guardado.getId());
        auditoria.setDescripcion("Ajuste de stock para producto: " + producto.getNombre() + " en almacén: " + almacen.getNombre() + ". Stock anterior: " + stockAnterior + ", nuevo: " + nuevoStock);
        auditoriaRepository.save(auditoria);

        return movimientoMapper.toResponse(guardado);
    }

    private void validarCantidadEnteraPositiva(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0 || cantidad.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("La cantidad sólo permite números enteros mayores a cero.");
        }
    }
}