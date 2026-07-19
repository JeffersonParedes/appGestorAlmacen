package com.gestoralmacen.app.dto.response;

public class DashboardBodegueroResponseDTO {
    private Double inventarioTotal;
    private Long productos;
    private Long stockMinimo;
    private Long productosVencer;
    private Long solicitudes;

    public DashboardBodegueroResponseDTO() {
    }

    public DashboardBodegueroResponseDTO(Double inventarioTotal, Long productos, Long stockMinimo, Long productosVencer, Long solicitudes) {
        this.inventarioTotal = inventarioTotal;
        this.productos = productos;
        this.stockMinimo = stockMinimo;
        this.productosVencer = productosVencer;
        this.solicitudes = solicitudes;
    }

    public Double getInventarioTotal() {
        return inventarioTotal;
    }

    public void setInventarioTotal(Double inventarioTotal) {
        this.inventarioTotal = inventarioTotal;
    }

    public Long getProductos() {
        return productos;
    }

    public void setProductos(Long productos) {
        this.productos = productos;
    }

    public Long getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Long stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Long getProductosVencer() {
        return productosVencer;
    }

    public void setProductosVencer(Long productosVencer) {
        this.productosVencer = productosVencer;
    }

    public Long getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(Long solicitudes) {
        this.solicitudes = solicitudes;
    }
}
