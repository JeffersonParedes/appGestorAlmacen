package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;

import java.util.List;

public interface EmpresaService {
    // Para que el Super Admin vea todas las empresas
    List<EmpresaResponseDTO> listarTodas();

    // Para ver el detalle de una sola
    EmpresaResponseDTO obtenerPorId(Long id);

    // Para registrar la empresa (y opcionalmente, le crearemos su dueño aquí mismo
    // o en un endpoint separado)
    EmpresaResponseDTO crearEmpresa(EmpresaRequestDTO requestDTO);

    // Para deshabilitar la empresa si dejan de pagar el servicio
    void cambiarEstado(Long id, String nuevoEstado);

    // Para actualizar la información de la empresa
    EmpresaResponseDTO actualizarEmpresa(Long id, EmpresaRequestDTO requestDTO);

    // Para consultar información estadística general de la empresa
    java.util.Map<String, Object> consultarInformacionGeneral(Long empresaId);
}