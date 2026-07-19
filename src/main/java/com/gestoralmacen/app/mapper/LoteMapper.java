package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.LoteRequestDTO;
import com.gestoralmacen.app.dto.response.LoteResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Lote;
import com.gestoralmacen.app.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class LoteMapper {

    public Lote toEntity(LoteRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Lote lote = new Lote();
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            lote.setEmpresa(empresa);
        }
        if (dto.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(dto.getProductoId());
            lote.setProducto(producto);
        }
        lote.setNumeroLote(dto.getNumeroLote());
        lote.setFechaFabricacion(dto.getFechaFabricacion());
        lote.setFechaVencimiento(dto.getFechaVencimiento());
        lote.setCantidadActual(dto.getCantidadActual());
        lote.setCostoCompra(dto.getCostoCompra());
        if (dto.getEstado() != null) {
            lote.setEstado(dto.getEstado());
        }
        return lote;
    }

    public LoteResponseDTO toResponse(Lote entity) {
        if (entity == null) {
            return null;
        }
        LoteResponseDTO dto = new LoteResponseDTO();
        dto.setId(entity.getId());
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
        }
        if (entity.getProducto() != null) {
            dto.setProductoId(entity.getProducto().getId());
            dto.setNombreProducto(entity.getProducto().getNombre());
        }
        dto.setNumeroLote(entity.getNumeroLote());
        dto.setFechaFabricacion(entity.getFechaFabricacion());
        dto.setFechaVencimiento(entity.getFechaVencimiento());
        dto.setCantidadActual(entity.getCantidadActual());
        dto.setCostoCompra(entity.getCostoCompra());
        dto.setEstado(entity.getEstado());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
