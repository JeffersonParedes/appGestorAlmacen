package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.CategoriaRequestDTO;
import com.gestoralmacen.app.dto.response.CategoriaResponseDTO;
import com.gestoralmacen.app.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequestDTO dto) {
        if (dto == null)
            return null;
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

    public CategoriaResponseDTO toResponse(Categoria entity) {
        if (entity == null)
            return null;
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEstadoAprobacion(entity.getEstadoAprobacion());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}
