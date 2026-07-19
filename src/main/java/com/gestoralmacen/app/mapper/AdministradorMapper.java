package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.AdministradorRequestDTO;
import com.gestoralmacen.app.dto.response.AdministradorResponseDTO;
import com.gestoralmacen.app.entity.Administrador;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {

    public Administrador toEntity(AdministradorRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Administrador admin = new Administrador();
        admin.setUsuario(dto.getUsuario());
        admin.setCorreo(dto.getCorreo());
        admin.setPassword(dto.getPassword());
        admin.setNombreCompleto(dto.getNombreCompleto());
        return admin;
    }

    public AdministradorResponseDTO toResponse(Administrador entity) {
        if (entity == null) {
            return null;
        }
        AdministradorResponseDTO dto = new AdministradorResponseDTO();
        dto.setId(entity.getId());
        dto.setUsuario(entity.getUsuario());
        dto.setCorreo(entity.getCorreo());
        dto.setNombreCompleto(entity.getNombreCompleto());
        dto.setUltimoAcceso(entity.getUltimoAcceso());
        dto.setActivo(entity.getActivo());
        return dto;
    }
}
