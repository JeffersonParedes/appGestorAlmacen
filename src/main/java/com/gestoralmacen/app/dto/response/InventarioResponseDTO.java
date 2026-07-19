package com.gestoralmacen.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventarioResponseDTO {
    private Long id;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private LocalDateTime ultimaActualizacion;

    private ProductoResponseDTO producto;
    private AlmacenResponseDTO almacen;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public ProductoResponseDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoResponseDTO producto) {
        this.producto = producto;
    }

    public AlmacenResponseDTO getAlmacen() {
        return almacen;
    }

    public void setAlmacen(AlmacenResponseDTO almacen) {
        this.almacen = almacen;
    }
}
