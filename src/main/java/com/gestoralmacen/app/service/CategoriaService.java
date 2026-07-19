package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.CategoriaRequestDTO;
import com.gestoralmacen.app.dto.response.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDTO> listarActivasPorEmpresa(Long empresaId);

    // Sugerida por el empleado (Entra como PENDIENTE) o creada directamente por Bodeguero (Entra como APROBADO)
    CategoriaResponseDTO sugerirCategoria(CategoriaRequestDTO requestDTO, Long empresaId);
    
    CategoriaResponseDTO registrarCategoria(CategoriaRequestDTO requestDTO, Long empresaId, Long usuarioId);

    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO requestDTO, Long empresaId);

    CategoriaResponseDTO obtenerPorId(Long id, Long empresaId);

    List<CategoriaResponseDTO> listarTodasPorEmpresa(Long empresaId);

    // Aprobada por el dueño
    void aprobarCategoria(Long id, Long empresaId);

    // Rechazada por el dueño
    void rechazarCategoria(Long id, Long empresaId);

    // Borrado lógico
    void eliminarCategoria(Long id, Long empresaId);
}

