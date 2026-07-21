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
        String usuarioStr = requestDTO.getUsuario() != null ? requestDTO.getUsuario().trim() : "";
        String correoStr = requestDTO.getCorreo() != null ? requestDTO.getCorreo().trim() : "";
        String dniStr = requestDTO.getDni() != null ? requestDTO.getDni().trim() : "";

        // 1. Validar nombre de usuario único
        if (usuarioRepository.existsByUsuario(usuarioStr)) {
            throw new ReglaNegocioException("El nombre de usuario ya se encuentra registrado.");
        }

        // 2. Validar correo único
        if (!correoStr.isEmpty() && usuarioRepository.existsByCorreo(correoStr)) {
            throw new ReglaNegocioException("El correo electrónico ya se encuentra registrado.");
        }

        // 3. Validar DNI único y formato (8 dígitos numéricos)
        if (!dniStr.isEmpty()) {
            if (dniStr.length() != 8 || !dniStr.matches("\\d+")) {
                throw new ReglaNegocioException("El DNI debe ser numérico y contener exactamente 8 dígitos.");
            }
            if (usuarioRepository.existsByDni(dniStr)) {
                throw new ReglaNegocioException("El DNI ingresado ya se encuentra registrado.");
            }
        }

        // 4. Validar contraseña mínima de 8 caracteres
        if (requestDTO.getContrasena() == null || requestDTO.getContrasena().length() < 8) {
            throw new ReglaNegocioException("La contraseña debe contener al menos 8 caracteres.");
        }

        Usuario nuevoUsuario = usuarioMapper.toEntity(requestDTO);
        nuevoUsuario.setUsuario(usuarioStr);
        nuevoUsuario.setCorreo(correoStr);
        nuevoUsuario.setDni(dniStr);
        nuevoUsuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));

        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));
            nuevoUsuario.setEmpresa(empresa);
        }

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

        String usuarioStr = requestDTO.getUsuario() != null ? requestDTO.getUsuario().trim() : "";
        String correoStr = requestDTO.getCorreo() != null ? requestDTO.getCorreo().trim() : "";
        String dniStr = requestDTO.getDni() != null ? requestDTO.getDni().trim() : "";

        if (!usuario.getUsuario().equalsIgnoreCase(usuarioStr)) {
            if (usuarioRepository.existsByUsuario(usuarioStr)) {
                throw new ReglaNegocioException("El nombre de usuario ya se encuentra registrado.");
            }
            usuario.setUsuario(usuarioStr);
        }

        if (!correoStr.isEmpty() && !correoStr.equalsIgnoreCase(usuario.getCorreo())) {
            if (usuarioRepository.existsByCorreo(correoStr)) {
                throw new ReglaNegocioException("El correo electrónico ya se encuentra registrado.");
            }
            usuario.setCorreo(correoStr);
        }

        if (!dniStr.isEmpty() && !dniStr.equalsIgnoreCase(usuario.getDni())) {
            if (dniStr.length() != 8 || !dniStr.matches("\\d+")) {
                throw new ReglaNegocioException("El DNI debe ser numérico y contener exactamente 8 dígitos.");
            }
            if (usuarioRepository.existsByDni(dniStr)) {
                throw new ReglaNegocioException("El DNI ingresado ya se encuentra registrado.");
            }
            usuario.setDni(dniStr);
        }

        if (requestDTO.getContrasena() != null && !requestDTO.getContrasena().trim().isEmpty()) {
            if (requestDTO.getContrasena().length() < 8) {
                throw new ReglaNegocioException("La contraseña debe contener al menos 8 caracteres.");
            }
            usuario.setContrasena(passwordEncoder.encode(requestDTO.getContrasena()));
        }

        usuario.setNombreCompleto(requestDTO.getNombreCompleto());
        usuario.setRol(requestDTO.getRol());

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(guardado);
    }
}
