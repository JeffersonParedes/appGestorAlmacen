package com.gestoralmacen.app.dto.response;

import java.time.LocalDateTime;

public class ReporteResponseDTO {
    private String nombreArchivo;
    private String tipoArchivo;
    private LocalDateTime fechaGeneracion;
    private Long tamañoArchivo;
    private byte[] archivo;

    public ReporteResponseDTO() {
    }

    public ReporteResponseDTO(String nombreArchivo, String tipoArchivo, LocalDateTime fechaGeneracion, Long tamañoArchivo, byte[] archivo) {
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.fechaGeneracion = fechaGeneracion;
        this.tamañoArchivo = tamañoArchivo;
        this.archivo = archivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Long getTamañoArchivo() {
        return tamañoArchivo;
    }

    public void setTamañoArchivo(Long tamañoArchivo) {
        this.tamañoArchivo = tamañoArchivo;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }
}
