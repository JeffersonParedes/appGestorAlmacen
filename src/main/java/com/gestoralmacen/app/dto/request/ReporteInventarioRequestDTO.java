package com.gestoralmacen.app.dto.request;

import java.time.LocalDate;

public class ReporteInventarioRequestDTO {
    private Long empresaId;
    private Long almacenId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean incluirStockMinimo;

    public ReporteInventarioRequestDTO() {
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getIncluirStockMinimo() {
        return incluirStockMinimo;
    }

    public void setIncluirStockMinimo(Boolean incluirStockMinimo) {
        this.incluirStockMinimo = incluirStockMinimo;
    }
}
