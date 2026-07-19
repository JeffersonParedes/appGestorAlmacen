package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.ProductoRequestDTO;
import com.gestoralmacen.app.dto.response.ProductoResponseDTO;
import com.gestoralmacen.app.entity.Categoria;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Producto;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.ProductoMapper;
import com.gestoralmacen.app.repository.CategoriaRepository;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.ProductoRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.repository.InventarioRepository;
import com.gestoralmacen.app.repository.LoteRepository;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final LoteRepository loteRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository, EmpresaRepository empresaRepository,
            CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository,
            InventarioRepository inventarioRepository, LoteRepository loteRepository,
            ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.loteRepository = loteRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarActivosPorEmpresa(Long empresaId) {
        return productoRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarBorradosPorEmpresa(Long empresaId) {
        return productoRepository.findByEmpresaIdAndEstado(empresaId, "ELIMINADO").stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDTO sugerirProducto(ProductoRequestDTO requestDTO, Long empresaId) {
        // Validar que el código de barras no exista en la misma empresa
        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().isEmpty()) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras())
                    .isPresent()) {
                throw new ReglaNegocioException("El código de barras ya existe en esta empresa.");
            }
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        // Asegurarse de que la categoría le pertenezca a la misma empresa
        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("La categoría no pertenece a tu empresa.");
        }

        Producto nuevoProducto = productoMapper.toEntity(requestDTO);
        nuevoProducto.setEmpresa(empresa);
        nuevoProducto.setCategoria(categoria);

        // ¡Magia! Entra como PENDIENTE para que el dueño lo revise
        nuevoProducto.setEstadoAprobacion("PENDIENTE");

        Producto guardado = productoRepository.save(nuevoProducto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void aprobarProducto(Long id, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        // Medida de seguridad Multi-tenant extrema
        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        producto.setEstadoAprobacion("APROBADO");
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        // ¡Soft Delete! El registro se queda en la BD para no romper el historial
        producto.setEstado("ELIMINADO");
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO requestDTO, Long empresaId, Long usuarioId) {
        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().isEmpty()) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras())
                    .isPresent()) {
                throw new ReglaNegocioException("El código de barras ya existe en esta empresa.");
            }
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("La categoría no pertenece a tu empresa.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El usuario no pertenece a la misma empresa.");
        }

        Producto nuevoProducto = productoMapper.toEntity(requestDTO);
        nuevoProducto.setEmpresa(empresa);
        nuevoProducto.setCategoria(categoria);

        if (usuario.getRol().equals("BODEGUERO")) {
            nuevoProducto.setEstadoAprobacion("APROBADO");
        } else if (usuario.getRol().equals("EMPLEADO")) {
            nuevoProducto.setEstadoAprobacion("PENDIENTE");
        } else {
            throw new ReglaNegocioException("Rol de usuario no autorizado para registrar productos.");
        }

        Producto guardado = productoRepository.save(nuevoProducto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().isEmpty() 
            && !requestDTO.getCodigoBarras().equals(producto.getCodigoBarras())) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras()).isPresent()) {
                throw new ReglaNegocioException("El código de barras ya existe en esta empresa.");
            }
            producto.setCodigoBarras(requestDTO.getCodigoBarras());
        }

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("La categoría no pertenece a tu empresa.");
        }

        producto.setNombre(requestDTO.getNombre());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setImagenUrl(requestDTO.getImagenUrl());
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public void rechazarProducto(Long id, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        producto.setEstadoAprobacion("RECHAZADO");
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> consultarProductosConStockMinimo(Long empresaId) {
        return inventarioRepository.findByEmpresaId(empresaId).stream()
                .filter(inv -> inv.getStockActual().compareTo(inv.getStockMinimo()) <= 0)
                .map(inv -> inv.getProducto())
                .distinct()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> consultarProductosProximosAVencer(Long empresaId, int diasThreshold) {
        java.time.LocalDate limitDate = java.time.LocalDate.now().plusDays(diasThreshold);
        return loteRepository.findByEmpresaId(empresaId).stream()
                .filter(l -> l.getFechaVencimiento() != null && !l.getFechaVencimiento().isBefore(java.time.LocalDate.now()) && l.getFechaVencimiento().isBefore(limitDate))
                .map(l -> l.getProducto())
                .distinct()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
