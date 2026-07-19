package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;

import java.util.List;
import java.util.Map;

public interface AdministradorService {
    EmpresaResponseDTO registrarEmpresaSaaS(EmpresaRequestDTO empresaDTO, 
                                            String usuarioBodeguero, 
                                            String correoBodeguero, 
                                            String contrasenaBodeguero, 
                                            String dniBodeguero, 
                                            String nombreBodeguero, 
                                            String planSuscripcion, 
                                            Double montoPago, 
                                            Integer duracionMeses);

    void suspenderEmpresa(Long empresaId);

    void reactivarEmpresa(Long empresaId);

    Map<String, Object> consultarEstadisticasSaaS();

    List<SuscripcionResponseDTO> consultarSuscripciones();

    void gestionarNotificacion(NotificacionRequestDTO dto);
}
