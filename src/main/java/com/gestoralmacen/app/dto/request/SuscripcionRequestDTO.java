package com.gestoralmacen.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SuscripcionRequestDTO {

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long empresaId;

    private String planSuscripcion; // BASICO | PRO | PREMIUM
    private String tipoSuscripcion; // PRIMER_REGISTRO | RENOVACION

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @PositiveOrZero(message = "El monto pagado debe ser positivo o cero")
    private BigDecimal montoPagado;

    private String estadoPago; // PENDIENTE | PAGADO | VENCIDO
    private String metodoPago;

    public SuscripcionRequestDTO() {
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getPlanSuscripcion() {
        return planSuscripcion;
    }

    public void setPlanSuscripcion(String planSuscripcion) {
        this.planSuscripcion = planSuscripcion;
    }

    public String getTipoSuscripcion() {
        return tipoSuscripcion;
    }

    public void setTipoSuscripcion(String tipoSuscripcion) {
        this.tipoSuscripcion = tipoSuscripcion;
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

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
