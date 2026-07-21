package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.CategoriaRequestDTO;
import com.gestoralmacen.app.dto.response.CategoriaResponseDTO;
import com.gestoralmacen.app.entity.Categoria;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.CategoriaMapper;
import com.gestoralmacen.app.repository.CategoriaRepository;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarActivasPorEmpresa(Long empresaId) {
        return categoriaRepository.findByEmpresaIdAndEstado(empresaId, "ACTIVO").stream()
                .map(categoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaResponseDTO sugerirCategoria(CategoriaRequestDTO requestDTO, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        String nombre = requestDTO.getNombre() != null ? requestDTO.getNombre().trim() : "";

        if (categoriaRepository.findByEmpresaIdAndNombre(empresaId, nombre).isPresent()) {
            throw new ReglaNegocioException("La categoría ya se encuentra registrada.");
        }

        Categoria nuevaCategoria = categoriaMapper.toEntity(requestDTO);
        nuevaCategoria.setEmpresa(empresa);
        nuevaCategoria.setEstadoAprobacion("PENDIENTE");

        Categoria guardada = categoriaRepository.save(nuevaCategoria);
        return categoriaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void aprobarCategoria(Long id, Long empresaId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta categoría.");
        }

        categoria.setEstadoAprobacion("APROBADO");
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Long id, Long empresaId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta categoría.");
        }

        categoria.setEstado("ELIMINADO");
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO registrarCategoria(CategoriaRequestDTO requestDTO, Long empresaId, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El usuario no pertenece a la misma empresa.");
        }

        String nombre = requestDTO.getNombre() != null ? requestDTO.getNombre().trim() : "";

        // Validar que el nombre no exista en la misma empresa
        if (categoriaRepository.findByEmpresaIdAndNombre(empresaId, nombre).isPresent()) {
            throw new ReglaNegocioException("La categoría ya se encuentra registrada.");
        }

        Categoria nuevaCategoria = categoriaMapper.toEntity(requestDTO);
        nuevaCategoria.setEmpresa(empresa);

        if (usuario.getRol().equals("BODEGUERO")) {
            nuevaCategoria.setEstadoAprobacion("APROBADO");
        } else if (usuario.getRol().equals("EMPLEADO")) {
            nuevaCategoria.setEstadoAprobacion("PENDIENTE");
        } else {
            throw new ReglaNegocioException("Rol de usuario no autorizado para registrar categorías.");
        }

        Categoria guardada = categoriaRepository.save(nuevaCategoria);
        return categoriaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO requestDTO, Long empresaId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta categoría.");
        }

        String nombre = requestDTO.getNombre() != null ? requestDTO.getNombre().trim() : "";

        categoriaRepository.findByEmpresaIdAndNombre(empresaId, nombre).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new ReglaNegocioException("La categoría ya se encuentra registrada.");
            }
        });

        categoria.setNombre(nombre);
        categoria.setDescripcion(requestDTO.getDescripcion());

        Categoria guardada = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id, Long empresaId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta categoría.");
        }

        return categoriaMapper.toResponse(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodasPorEmpresa(Long empresaId) {
        return categoriaRepository.findAll().stream()
                .filter(c -> c.getEmpresa().getId().equals(empresaId))
                .map(categoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void rechazarCategoria(Long id, Long empresaId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        if (!categoria.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre esta categoría.");
        }

        categoria.setEstadoAprobacion("RECHAZADO");
        categoriaRepository.save(categoria);
    }
}