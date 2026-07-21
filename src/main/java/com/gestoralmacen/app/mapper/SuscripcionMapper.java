package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.SuscripcionRequestDTO;
import com.gestoralmacen.app.dto.response.SuscripcionResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Suscripcion;
import org.springframework.stereotype.Component;

@Component
public class SuscripcionMapper {

    public Suscripcion toEntity(SuscripcionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Suscripcion suscripcion = new Suscripcion();
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            suscripcion.setEmpresa(empresa);
        }
        if (dto.getPlanSuscripcion() != null) {
            suscripcion.setPlanSuscripcion(dto.getPlanSuscripcion());
        }
        if (dto.getTipoSuscripcion() != null) {
            suscripcion.setTipoSuscripcion(dto.getTipoSuscripcion());
        }
        suscripcion.setFechaInicio(dto.getFechaInicio());
        suscripcion.setFechaFin(dto.getFechaFin());
        suscripcion.setMontoPagado(dto.getMontoPagado());
        if (dto.getEstadoPago() != null) {
            suscripcion.setEstadoPago(dto.getEstadoPago());
        }
        suscripcion.setMetodoPago(dto.getMetodoPago());
        return suscripcion;
    }

    public SuscripcionResponseDTO toResponse(Suscripcion entity) {
        if (entity == null) {
            return null;
        }
        SuscripcionResponseDTO dto = new SuscripcionResponseDTO();
        dto.setId(entity.getId());
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
            dto.setRazonSocialEmpresa(entity.getEmpresa().getRazonSocial());
        }
        dto.setPlanSuscripcion(entity.getPlanSuscripcion());
        dto.setTipoSuscripcion(entity.getTipoSuscripcion());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setMontoPagado(entity.getMontoPagado());
        dto.setEstadoPago(entity.getEstadoPago());
        dto.setMetodoPago(entity.getMetodoPago());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
