package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.ProductoRequestDTO;
import com.gestoralmacen.app.dto.response.ProductoResponseDTO;
import com.gestoralmacen.app.entity.Categoria;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Producto;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.ProductoMapper;
import com.gestoralmacen.app.repository.CategoriaRepository;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.ProductoRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                                EmpresaRepository empresaRepository,
                                CategoriaRepository categoriaRepository,
                                UsuarioRepository usuarioRepository,
                                ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
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
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodosPorEmpresa(Long empresaId) {
        return productoRepository.findByEmpresaId(empresaId).stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarAprobadosPorEmpresa(Long empresaId) {
        return productoRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").stream()
                .filter(p -> "APROBADO".equalsIgnoreCase(p.getEstadoAprobacion()))
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> consultarProductosConStockMinimo(Long empresaId) {
        return productoRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").stream()
                .filter(p -> p.getStockMinimo() != null)
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> consultarProductosProximosAVencer(Long empresaId, int diasThreshold) {
        return productoRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDTO sugerirProducto(ProductoRequestDTO requestDTO, Long empresaId) {
        validarCamposProducto(requestDTO);

        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().trim().isEmpty()) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras().trim()).isPresent()) {
                throw new ReglaNegocioException("El código de barras ya se encuentra registrado.");
            }
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("La categoría no pertenece a tu empresa.");
        }

        Producto nuevoProducto = productoMapper.toEntity(requestDTO);
        nuevoProducto.setEmpresa(empresa);
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setEstadoAprobacion("PENDIENTE");

        Producto guardado = productoRepository.save(nuevoProducto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void aprobarProducto(Long id, Long empresaId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

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

        producto.setEstado("ELIMINADO");
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO requestDTO, Long empresaId, Long usuarioId) {
        validarCamposProducto(requestDTO);

        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().trim().isEmpty()) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras().trim()).isPresent()) {
                throw new ReglaNegocioException("El código de barras ya se encuentra registrado.");
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

        if ("BODEGUERO".equalsIgnoreCase(usuario.getRol())) {
            nuevoProducto.setEstadoAprobacion("APROBADO");
        } else if ("EMPLEADO".equalsIgnoreCase(usuario.getRol())) {
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
        validarCamposProducto(requestDTO);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este producto.");
        }

        if (requestDTO.getCodigoBarras() != null && !requestDTO.getCodigoBarras().trim().isEmpty() 
            && !requestDTO.getCodigoBarras().trim().equals(producto.getCodigoBarras())) {
            if (productoRepository.findByEmpresaIdAndCodigoBarras(empresaId, requestDTO.getCodigoBarras().trim()).isPresent()) {
                throw new ReglaNegocioException("El código de barras ya se encuentra registrado.");
            }
            producto.setCodigoBarras(requestDTO.getCodigoBarras().trim());
        }

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("La categoría no pertenece a tu empresa.");
        }

        producto.setNombre(requestDTO.getNombre().trim());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        if (requestDTO.getStockMinimo() != null) {
            producto.setStockMinimo(requestDTO.getStockMinimo());
        }
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

    private void validarCamposProducto(ProductoRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        }
        if (dto.getPrecio() == null || dto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio unitario debe ser mayor a cero.");
        }
        if (dto.getStockMinimo() != null && dto.getStockMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo.");
        }
    }
}
