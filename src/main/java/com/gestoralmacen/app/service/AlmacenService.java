package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.AlmacenRequestDTO;
import com.gestoralmacen.app.dto.response.AlmacenResponseDTO;

import java.util.List;

public interface AlmacenService {
    List<AlmacenResponseDTO> listarPorEmpresa(Long empresaId);

    AlmacenResponseDTO crearAlmacen(AlmacenRequestDTO requestDTO, Long empresaId);

    void eliminarAlmacen(Long id, Long empresaId);

    AlmacenResponseDTO actualizarAlmacen(Long id, AlmacenRequestDTO requestDTO, Long empresaId);

    void activarAlmacen(Long id, Long empresaId);

    void desactivarAlmacen(Long id, Long empresaId);
}

