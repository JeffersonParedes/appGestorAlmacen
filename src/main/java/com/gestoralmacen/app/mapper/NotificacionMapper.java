package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.NotificacionRequestDTO;
import com.gestoralmacen.app.dto.response.NotificacionResponseDTO;
import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Notificacion;
import com.gestoralmacen.app.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public Notificacion toEntity(NotificacionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Notificacion notificacion = new Notificacion();
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            notificacion.setEmpresa(empresa);
        }
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            notificacion.setUsuario(usuario);
        }
        if (dto.getAdministradorId() != null) {
            Administrador admin = new Administrador();
            admin.setId(dto.getAdministradorId());
            notificacion.setAdministrador(admin);
        }
        notificacion.setTipo(dto.getTipo());
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        if (dto.getLeido() != null) {
            notificacion.setLeido(dto.getLeido());
        }
        notificacion.setFechaLectura(dto.getFechaLectura());
        return notificacion;
    }

    public NotificacionResponseDTO toResponse(Notificacion entity) {
        if (entity == null) {
            return null;
        }
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(entity.getId());
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
        }
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
        }
        if (entity.getAdministrador() != null) {
            dto.setAdministradorId(entity.getAdministrador().getId());
        }
        dto.setTipo(entity.getTipo());
        dto.setTitulo(entity.getTitulo());
        dto.setMensaje(entity.getMensaje());
        dto.setLeido(entity.getLeido());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaLectura(entity.getFechaLectura());
        return dto;
    }
}
