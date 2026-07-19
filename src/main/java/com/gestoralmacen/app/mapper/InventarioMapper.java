package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.entity.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    private final ProductoMapper productoMapper;
    private final AlmacenMapper almacenMapper;

    public InventarioMapper(ProductoMapper productoMapper, AlmacenMapper almacenMapper) {
        this.productoMapper = productoMapper;
        this.almacenMapper = almacenMapper;
    }

    public InventarioResponseDTO toResponse(Inventario entity) {
        if (entity == null)
            return null;
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(entity.getId());
        dto.setStockActual(entity.getStockActual());
        dto.setStockMinimo(entity.getStockMinimo());
        dto.setUltimaActualizacion(entity.getUltimaActualizacion());

        dto.setProducto(productoMapper.toResponse(entity.getProducto()));
        dto.setAlmacen(almacenMapper.toResponse(entity.getAlmacen()));
        return dto;
    }
}
