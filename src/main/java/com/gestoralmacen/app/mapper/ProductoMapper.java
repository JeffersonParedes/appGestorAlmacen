package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.ProductoRequestDTO;
import com.gestoralmacen.app.dto.response.ProductoResponseDTO;
import com.gestoralmacen.app.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    private final CategoriaMapper categoriaMapper;

    public ProductoMapper(CategoriaMapper categoriaMapper) {
        this.categoriaMapper = categoriaMapper;
    }

    public Producto toEntity(ProductoRequestDTO dto) {
        if (dto == null)
            return null;
        Producto producto = new Producto();
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setImagenUrl(dto.getImagenUrl());
        return producto;
    }

    public ProductoResponseDTO toResponse(Producto entity) {
        if (entity == null)
            return null;
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(entity.getId());
        dto.setCodigoBarras(entity.getCodigoBarras());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecio(entity.getPrecio());
        dto.setStockMinimo(entity.getStockMinimo());
        dto.setImagenUrl(entity.getImagenUrl());
        dto.setEstadoAprobacion(entity.getEstadoAprobacion());
        dto.setEstado(entity.getEstado());

        dto.setCategoria(categoriaMapper.toResponse(entity.getCategoria()));
        return dto;
    }
}