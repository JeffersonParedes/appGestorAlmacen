package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.EmpresaMapper;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.AlmacenRepository;
import com.gestoralmacen.app.repository.CategoriaRepository;
import com.gestoralmacen.app.repository.ProductoRepository;
import com.gestoralmacen.app.repository.InventarioRepository;
import com.gestoralmacen.app.service.EmpresaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    private final AlmacenRepository almacenRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    // Inyección de dependencias por constructor (mejor práctica)
    public EmpresaServiceImpl(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper,
            AlmacenRepository almacenRepository, CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository, InventarioRepository inventarioRepository) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
        this.almacenRepository = almacenRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarTodas() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponseDTO obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la empresa con ID: " + id));
        return empresaMapper.toResponse(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO crearEmpresa(EmpresaRequestDTO requestDTO) {
        // Regla de Negocio: Validar que el RUC no exista previamente
        if (empresaRepository.findByRuc(requestDTO.getRuc()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una empresa registrada con el RUC: " + requestDTO.getRuc());
        }

        // 1. Convertir DTO a Entidad
        Empresa nuevaEmpresa = empresaMapper.toEntity(requestDTO);

        // 3. Guardar en BD

        Empresa empresaGuardada = empresaRepository.save(nuevaEmpresa);

        // 4. Convertir a DTO de Respuesta
        return empresaMapper.toResponse(empresaGuardada);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, String nuevoEstado) {
        // Validar que el estado enviado sea válido
        if (!nuevoEstado.equals("ACTIVO") && !nuevoEstado.equals("SUSPENDIDO") && !nuevoEstado.equals("INACTIVO")) {
            throw new ReglaNegocioException("El estado '" + nuevoEstado + "' no es válido.");
        }

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la empresa con ID: " + id));

        empresa.setEstado(nuevoEstado);
        empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizarEmpresa(Long id, EmpresaRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la empresa con ID: " + id));

        if (!empresa.getRuc().equals(requestDTO.getRuc())) {
            if (empresaRepository.findByRuc(requestDTO.getRuc()).isPresent()) {
                throw new ReglaNegocioException("Ya existe una empresa registrada con el RUC: " + requestDTO.getRuc());
            }
            empresa.setRuc(requestDTO.getRuc());
        }

        empresa.setRazonSocial(requestDTO.getRazonSocial());
        empresa.setDireccionPrincipal(requestDTO.getDireccionPrincipal());
        empresa.setTelefonoContacto(requestDTO.getTelefonoContacto());
        empresa.setCorreoContacto(requestDTO.getCorreoContacto());

        Empresa guardada = empresaRepository.save(empresa);
        return empresaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> consultarInformacionGeneral(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new RecursoNoEncontradoException("No se encontró la empresa con ID: " + empresaId);
        }

        long totalAlmacenes = almacenRepository.findByEmpresaId(empresaId).stream()
                .filter(a -> !a.getEstado().equals("INACTIVO")).count();
        long totalCategorias = categoriaRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").size();
        long totalProductos = productoRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").size();

        java.math.BigDecimal valorTotalInventario = inventarioRepository.findByEmpresaId(empresaId).stream()
                .map(i -> i.getStockActual().multiply(i.getProducto().getPrecio()))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalAlmacenes", totalAlmacenes);
        stats.put("totalCategorias", totalCategorias);
        stats.put("totalProductos", totalProductos);
        stats.put("valorTotalInventario", valorTotalInventario);
        return stats;
    }
}
