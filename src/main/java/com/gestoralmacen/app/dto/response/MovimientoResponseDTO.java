package com.gestoralmacen.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoResponseDTO {
    private Long id;
    private String tipoMovimiento;
    private BigDecimal cantidad;
    private LocalDateTime fechaMovimiento;
    private String motivo;

    private ProductoResponseDTO producto;
    private AlmacenResponseDTO almacen;
    private String nombreUsuario; // Solo el nombre de quién lo hizo, no todo su objeto Usuario
    private Long loteId;
    private String numeroLote;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }
}