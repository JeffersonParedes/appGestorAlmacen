package com.gestoralmacen.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitudRequestDTO {

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El tipo de solicitud es obligatorio")
    @Size(max = 50, message = "El tipo no puede exceder los 50 caracteres")
    private String tipo; // CREAR_PRODUCTO | EDITAR_PRODUCTO | etc.

    @NotNull(message = "El ID de referencia es obligatorio")
    private Long referenciaId;

    @Size(max = 20, message = "El estado no puede exceder los 20 caracteres")
    private String estado; // PENDIENTE | APROBADA | RECHAZADA

    private String observacion;

    // Valores propuestos para EDITAR_PRODUCTO / PRODUCTO (solo se aplican al aprobar)
    @Size(max = 150, message = "El nombre propuesto no puede exceder los 150 caracteres")
    private String nombrePropuesto;

    private BigDecimal precioPropuesto;

    private Long aprobadoPor; // ID del usuario aprobador

    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaRespuesta;

    public SolicitudRequestDTO() {
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getNombrePropuesto() {
        return nombrePropuesto;
    }

    public void setNombrePropuesto(String nombrePropuesto) {
        this.nombrePropuesto = nombrePropuesto;
    }

    public BigDecimal getPrecioPropuesto() {
        return precioPropuesto;
    }

    public void setPrecioPropuesto(BigDecimal precioPropuesto) {
        this.precioPropuesto = precioPropuesto;
    }

    public Long getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Long aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}