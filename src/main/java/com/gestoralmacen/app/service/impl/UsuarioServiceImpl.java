package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.UsuarioRequestDTO;
import com.gestoralmacen.app.dto.response.UsuarioResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.UsuarioMapper;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.service.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioMapper usuarioMapper;
    // OJO: Luego inyectaremos PasswordEncoder de Spring Security para el Hash
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository,
            UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId).stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO, Long empresaId, Long creadorId) {
        // 1. Validar que el username (login) no exista ya en todo el sistema SaaS
        if (usuarioRepository.findByUsuario(requestDTO.getUsuario()).isPresent()) {
            throw new ReglaNegocioException("El nombre de usuario ya está en uso.");
        }

        Usuario nuevoUsuario = usuarioMapper.toEntity(requestDTO);

        // TODO: Encriptar la contraseña (lo haremos cuando configuremos Spring
        // Security)
        // ¡Magia de encriptación activada!
        nuevoUsuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));
        // nuevoUsuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));

        // 2. Asociar Empresa (Si el que se crea es el Admin Supremo, empresaId vendrá
        // nulo)
        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
            nuevoUsuario.setEmpresa(empresa);
        }

        // 3. Asociar al Creador (Quién registró a este usuario)
        if (creadorId != null) {
            Usuario creador = usuarioRepository.findById(creadorId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario creador no encontrado"));
            nuevoUsuario.setCreatedBy(creador);
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return usuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (!usuario.getUsuario().equals(requestDTO.getUsuario())) {
            if (usuarioRepository.findByUsuario(requestDTO.getUsuario()).isPresent()) {
                throw new ReglaNegocioException("El nombre de usuario ya está en uso.");
            }
            usuario.setUsuario(requestDTO.getUsuario());
        }

        if (requestDTO.getContrasena() != null && !requestDTO.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));
        }

        usuario.setNombreCompleto(requestDTO.getNombreCompleto());
        usuario.setRol(requestDTO.getRol());
        usuario.setDni(requestDTO.getDni());
        usuario.setCorreo(requestDTO.getCorreo());

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(guardado);
    }

}
