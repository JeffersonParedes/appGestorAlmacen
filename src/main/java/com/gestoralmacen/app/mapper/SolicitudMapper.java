package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.SolicitudRequestDTO;
import com.gestoralmacen.app.dto.response.SolicitudResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Solicitud;
import com.gestoralmacen.app.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public Solicitud toEntity(SolicitudRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Solicitud solicitud = new Solicitud();
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            solicitud.setEmpresa(empresa);
        }
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            solicitud.setUsuario(usuario);
        }
        solicitud.setTipo(dto.getTipo());
        solicitud.setReferenciaId(dto.getReferenciaId());
        if (dto.getEstado() != null) {
            solicitud.setEstado(dto.getEstado());
        }
        solicitud.setObservacion(dto.getObservacion());
        solicitud.setNombrePropuesto(dto.getNombrePropuesto());
        solicitud.setPrecioPropuesto(dto.getPrecioPropuesto());
        if (dto.getAprobadoPor() != null) {
            Usuario aprobador = new Usuario();
            aprobador.setId(dto.getAprobadoPor());
            solicitud.setAprobadoPor(aprobador);
        }
        solicitud.setFechaSolicitud(dto.getFechaSolicitud());
        solicitud.setFechaRespuesta(dto.getFechaRespuesta());
        return solicitud;
    }

    public SolicitudResponseDTO toResponse(Solicitud entity) {
        if (entity == null) {
            return null;
        }
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(entity.getId());
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
        }
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            dto.setNombreUsuario(entity.getUsuario().getNombreCompleto());
        }
        dto.setTipo(entity.getTipo());
        dto.setReferenciaId(entity.getReferenciaId());
        dto.setEstado(entity.getEstado());
        dto.setObservacion(entity.getObservacion());
        dto.setNombrePropuesto(entity.getNombrePropuesto());
        dto.setPrecioPropuesto(entity.getPrecioPropuesto());
        if (entity.getAprobadoPor() != null) {
            dto.setAprobadoPorId(entity.getAprobadoPor().getId());
            dto.setNombreAprobador(entity.getAprobadoPor().getNombreCompleto());
        }
        dto.setFechaSolicitud(entity.getFechaSolicitud());
        dto.setFechaRespuesta(entity.getFechaRespuesta());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}