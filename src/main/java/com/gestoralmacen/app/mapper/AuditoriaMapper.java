package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.AuditoriaRequestDTO;
import com.gestoralmacen.app.dto.response.AuditoriaResponseDTO;
import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.entity.Auditoria;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public Auditoria toEntity(AuditoriaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Auditoria auditoria = new Auditoria();
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            auditoria.setUsuario(usuario);
        }
        if (dto.getAdministradorId() != null) {
            Administrador admin = new Administrador();
            admin.setId(dto.getAdministradorId());
            auditoria.setAdministrador(admin);
        }
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            auditoria.setEmpresa(empresa);
        }
        auditoria.setAccion(dto.getAccion());
        auditoria.setTablaAfectada(dto.getTablaAfectada());
        auditoria.setRegistroId(dto.getRegistroId());
        auditoria.setDescripcion(dto.getDescripcion());
        auditoria.setIp(dto.getIp());
        return auditoria;
    }

    public AuditoriaResponseDTO toResponse(Auditoria entity) {
        if (entity == null) {
            return null;
        }
        AuditoriaResponseDTO dto = new AuditoriaResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            dto.setNombreUsuario(entity.getUsuario().getNombreCompleto());
        }
        if (entity.getAdministrador() != null) {
            dto.setAdministradorId(entity.getAdministrador().getId());
            dto.setNombreAdministrador(entity.getAdministrador().getNombreCompleto());
        }
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
            dto.setRazonSocialEmpresa(entity.getEmpresa().getRazonSocial());
        }
        dto.setAccion(entity.getAccion());
        dto.setTablaAfectada(entity.getTablaAfectada());
        dto.setRegistroId(entity.getRegistroId());
        dto.setDescripcion(entity.getDescripcion());
        dto.setIp(entity.getIp());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
