package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.EmpresaRequestDTO;
import com.gestoralmacen.app.dto.response.EmpresaResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(EmpresaRequestDTO dto) {
        if (dto == null)
            return null;
        Empresa empresa = new Empresa();
        empresa.setRuc(dto.getRuc());
        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setDireccionPrincipal(dto.getDireccionPrincipal());
        empresa.setTelefonoContacto(dto.getTelefonoContacto());
        empresa.setCorreoContacto(dto.getCorreoContacto());
        return empresa;
    }

    public EmpresaResponseDTO toResponse(Empresa entity) {
        if (entity == null)
            return null;
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(entity.getId());
        dto.setRuc(entity.getRuc());
        dto.setRazonSocial(entity.getRazonSocial());
        dto.setDireccionPrincipal(entity.getDireccionPrincipal());
        dto.setTelefonoContacto(entity.getTelefonoContacto());
        dto.setCorreoContacto(entity.getCorreoContacto());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}