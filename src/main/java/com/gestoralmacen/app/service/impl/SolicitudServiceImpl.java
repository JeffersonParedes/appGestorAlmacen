package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.SolicitudRequestDTO;
import com.gestoralmacen.app.dto.response.SolicitudResponseDTO;
import com.gestoralmacen.app.entity.*;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.SolicitudMapper;
import com.gestoralmacen.app.repository.*;
import com.gestoralmacen.app.service.SolicitudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudMapper solicitudMapper;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudMapper solicitudMapper,
                                EmpresaRepository empresaRepository,
                                UsuarioRepository usuarioRepository,
                                ProductoRepository productoRepository,
                                CategoriaRepository categoriaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudMapper = solicitudMapper;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public SolicitudResponseDTO crearSolicitud(SolicitudRequestDTO dto, Long empresaId, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Solicitud solicitud = solicitudMapper.toEntity(dto);
        solicitud.setEmpresa(empresa);
        solicitud.setUsuario(usuario);
        solicitud.setEstado("PENDIENTE");

        if ("PRODUCTO".equalsIgnoreCase(dto.getTipo())) {
            Producto producto = productoRepository.findById(dto.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto de referencia no encontrado"));
            solicitud.setObservacion("Solicitud de aprobación para producto: " + producto.getNombre());
        } else if ("CATEGORIA".equalsIgnoreCase(dto.getTipo())) {
            Categoria categoria = categoriaRepository.findById(dto.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de referencia no encontrada"));
            solicitud.setObservacion("Solicitud de aprobación para categoría: " + categoria.getNombre());
        } else {
            throw new ReglaNegocioException("Tipo de solicitud no válido: " + dto.getTipo());
        }

        Solicitud guardada = solicitudRepository.save(solicitud);
        return solicitudMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudResponseDTO obtenerPorId(Long id, Long empresaId) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));

        if (!solicitud.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta solicitud.");
        }

        return solicitudMapper.toResponse(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> listarPorEmpresa(Long empresaId) {
        return solicitudRepository.findByEmpresaId(empresaId).stream()
                .map(solicitudMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void procesarSolicitud(Long id, String estado, String motivoRechazo, Long empresaId) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));

        if (!solicitud.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta solicitud.");
        }

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new ReglaNegocioException("Esta solicitud ya ha sido procesada.");
        }

        if (!"APROBADO".equals(estado) && !"RECHAZADO".equals(estado)) {
            throw new ReglaNegocioException("Estado final no válido: " + estado);
        }

        solicitud.setEstado(estado);
        solicitud.setObservacion(motivoRechazo);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        if ("PRODUCTO".equalsIgnoreCase(solicitud.getTipo())) {
            Producto producto = productoRepository.findById(solicitud.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto de referencia no encontrado"));
            producto.setEstadoAprobacion(estado);
            productoRepository.save(producto);
        } else if ("CATEGORIA".equalsIgnoreCase(solicitud.getTipo())) {
            Categoria categoria = categoriaRepository.findById(solicitud.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de referencia no encontrada"));
            categoria.setEstadoAprobacion(estado);
            categoriaRepository.save(categoria);
        }
    }
}
