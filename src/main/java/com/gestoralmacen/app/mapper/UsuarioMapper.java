package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.UsuarioRequestDTO;
import com.gestoralmacen.app.dto.response.UsuarioResponseDTO;
import com.gestoralmacen.app.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null)
            return null;
        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        // OJO: La contraseña la encriptaremos en el Service, no en el Mapper
        usuario.setContrasena(dto.getContrasena());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setRol(dto.getRol());
        usuario.setDni(dto.getDni());
        usuario.setCorreo(dto.getCorreo());
        return usuario;
    }

    public UsuarioResponseDTO toResponse(Usuario entity) {
        if (entity == null)
            return null;
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(entity.getId());
        dto.setUsuario(entity.getUsuario());
        dto.setNombreCompleto(entity.getNombreCompleto());
        dto.setRol(entity.getRol());
        dto.setActivo(entity.getActivo());
        dto.setUltimoAcceso(entity.getUltimoAcceso());
        dto.setDni(entity.getDni());
        dto.setCorreo(entity.getCorreo());

        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
        }
        return dto;
    }
}
