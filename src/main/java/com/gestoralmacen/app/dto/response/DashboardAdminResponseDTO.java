package com.gestoralmacen.app.dto.response;

public class DashboardAdminResponseDTO {
    private Long empresasActivas;
    private Long empresasSuspendidas;
    private Long suscripcionesPorVencer;
    private Long nuevasEmpresasMes;

    public DashboardAdminResponseDTO() {
    }

    public DashboardAdminResponseDTO(Long empresasActivas, Long empresasSuspendidas, Long suscripcionesPorVencer, Long nuevasEmpresasMes) {
        this.empresasActivas = empresasActivas;
        this.empresasSuspendidas = empresasSuspendidas;
        this.suscripcionesPorVencer = suscripcionesPorVencer;
        this.nuevasEmpresasMes = nuevasEmpresasMes;
    }

    public Long getEmpresasActivas() {
        return empresasActivas;
    }

    public void setEmpresasActivas(Long empresasActivas) {
        this.empresasActivas = empresasActivas;
    }

    public Long getEmpresasSuspendidas() {
        return empresasSuspendidas;
    }

    public void setEmpresasSuspendidas(Long empresasSuspendidas) {
        this.empresasSuspendidas = empresasSuspendidas;
    }

    public Long getSuscripcionesPorVencer() {
        return suscripcionesPorVencer;
    }

    public void setSuscripcionesPorVencer(Long suscripcionesPorVencer) {
        this.suscripcionesPorVencer = suscripcionesPorVencer;
    }

    public Long getNuevasEmpresasMes() {
        return nuevasEmpresasMes;
    }

    public void setNuevasEmpresasMes(Long nuevasEmpresasMes) {
        this.nuevasEmpresasMes = nuevasEmpresasMes;
    }
}
