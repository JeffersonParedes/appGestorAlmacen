package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.entity.Inventario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
        
        BigDecimal stockMin = entity.getStockMinimo();
        if ((stockMin == null || stockMin.compareTo(BigDecimal.ZERO) == 0) && entity.getProducto() != null && entity.getProducto().getStockMinimo() != null) {
            stockMin = entity.getProducto().getStockMinimo();
        }
        dto.setStockMinimo(stockMin != null ? stockMin : BigDecimal.ZERO);
        dto.setUltimaActualizacion(entity.getUltimaActualizacion());

        dto.setProducto(productoMapper.toResponse(entity.getProducto()));
        dto.setAlmacen(almacenMapper.toResponse(entity.getAlmacen()));
        return dto;
    }
}
