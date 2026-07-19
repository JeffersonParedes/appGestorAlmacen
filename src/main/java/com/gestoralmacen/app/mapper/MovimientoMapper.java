package com.gestoralmacen.app.mapper;

import com.gestoralmacen.app.dto.request.MovimientoRequestDTO;
import com.gestoralmacen.app.dto.response.MovimientoResponseDTO;
import com.gestoralmacen.app.entity.HistorialMovimientos;
import com.gestoralmacen.app.entity.Lote;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {

    private final ProductoMapper productoMapper;
    private final AlmacenMapper almacenMapper;

    public MovimientoMapper(ProductoMapper productoMapper, AlmacenMapper almacenMapper) {
        this.productoMapper = productoMapper;
        this.almacenMapper = almacenMapper;
    }

    public HistorialMovimientos toEntity(MovimientoRequestDTO dto) {
        if (dto == null)
            return null;
        HistorialMovimientos movimiento = new HistorialMovimientos();
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setMotivo(dto.getMotivo());
        if (dto.getLoteId() != null) {
            Lote lote = new Lote();
            lote.setId(dto.getLoteId());
            movimiento.setLote(lote);
        }
        return movimiento;
    }

    public MovimientoResponseDTO toResponse(HistorialMovimientos entity) {
        if (entity == null)
            return null;
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setId(entity.getId());
        dto.setTipoMovimiento(entity.getTipoMovimiento());
        dto.setCantidad(entity.getCantidad());
        dto.setFechaMovimiento(entity.getFechaMovimiento());
        dto.setMotivo(entity.getMotivo());

        dto.setProducto(productoMapper.toResponse(entity.getProducto()));
        dto.setAlmacen(almacenMapper.toResponse(entity.getAlmacen()));

        if (entity.getUsuario() != null) {
            dto.setNombreUsuario(entity.getUsuario().getNombreCompleto());
        }

        if (entity.getLote() != null) {
            dto.setLoteId(entity.getLote().getId());
            dto.setNumeroLote(entity.getLote().getNumeroLote());
        }

        return dto;
    }
}