package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.UsuarioRequestDTO;
import com.gestoralmacen.app.dto.response.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    // Para que el Super Admin o el Bodeguero vea a su gente
    List<UsuarioResponseDTO> listarPorEmpresa(Long empresaId);

    // Aquí el Admin crea al dueño, o el dueño crea a su empleado
    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO, Long empresaId, Long creadorId);

    void desactivarUsuario(Long id);

    void activarUsuario(Long id);

    UsuarioResponseDTO obtenerPorId(Long id);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO);
}