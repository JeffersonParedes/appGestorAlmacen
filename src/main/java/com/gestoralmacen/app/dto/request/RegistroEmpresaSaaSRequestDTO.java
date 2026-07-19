package com.gestoralmacen.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistroEmpresaSaaSRequestDTO {

    @NotNull(message = "La información de la empresa es obligatoria")
    @Valid
    private EmpresaRequestDTO empresa;

    @NotBlank(message = "El nombre de usuario del bodeguero es obligatorio")
    @Size(max = 60)
    private String usuarioBodeguero;

    @NotBlank(message = "El correo del bodeguero es obligatorio")
    @Email
    @Size(max = 120)
    private String correoBodeguero;

    @NotBlank(message = "La contraseña del bodeguero es obligatoria")
    @Size(min = 6, max = 255)
    private String contrasenaBodeguero;

    @NotBlank(message = "El DNI del bodeguero es obligatorio")
    @Size(max = 20)
    private String dniBodeguero;

    @NotBlank(message = "El nombre completo del bodeguero es obligatorio")
    @Size(max = 120)
    private String nombreBodeguero;

    @NotBlank(message = "El plan de suscripción es obligatorio")
    private String planSuscripcion;

    @NotNull(message = "El monto de pago es obligatorio")
    private Double montoPago;

    @NotNull(message = "La duración en meses es obligatoria")
    private Integer duracionMeses;

    public RegistroEmpresaSaaSRequestDTO() {
    }

    public EmpresaRequestDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaRequestDTO empresa) {
        this.empresa = empresa;
    }

    public String getUsuarioBodeguero() {
        return usuarioBodeguero;
    }

    public void setUsuarioBodeguero(String usuarioBodeguero) {
        this.usuarioBodeguero = usuarioBodeguero;
    }

    public String getCorreoBodeguero() {
        return correoBodeguero;
    }

    public void setCorreoBodeguero(String correoBodeguero) {
        this.correoBodeguero = correoBodeguero;
    }

    public String getContrasenaBodeguero() {
        return contrasenaBodeguero;
    }

    public void setContrasenaBodeguero(String contrasenaBodeguero) {
        this.contrasenaBodeguero = contrasenaBodeguero;
    }

    public String getDniBodeguero() {
        return dniBodeguero;
    }

    public void setDniBodeguero(String dniBodeguero) {
        this.dniBodeguero = dniBodeguero;
    }

    public String getNombreBodeguero() {
        return nombreBodeguero;
    }

    public void setNombreBodeguero(String nombreBodeguero) {
        this.nombreBodeguero = nombreBodeguero;
    }

    public String getPlanSuscripcion() {
        return planSuscripcion;
    }

    public void setPlanSuscripcion(String planSuscripcion) {
        this.planSuscripcion = planSuscripcion;
    }

    public Double getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(Double montoPago) {
        this.montoPago = montoPago;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }
}
