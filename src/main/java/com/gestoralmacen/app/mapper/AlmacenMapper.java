package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.AlmacenRequestDTO;
import com.gestoralmacen.app.dto.response.AlmacenResponseDTO;
import com.gestoralmacen.app.entity.Almacen;
import org.springframework.stereotype.Component;

@Component
public class AlmacenMapper {

    public Almacen toEntity(AlmacenRequestDTO dto) {
        if (dto == null)
            return null;
        Almacen almacen = new Almacen();
        almacen.setNombre(dto.getNombre());
        almacen.setDireccion(dto.getDireccion());
        return almacen;
    }

    public AlmacenResponseDTO toResponse(Almacen entity) {
        if (entity == null)
            return null;
        AlmacenResponseDTO dto = new AlmacenResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccion());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}
