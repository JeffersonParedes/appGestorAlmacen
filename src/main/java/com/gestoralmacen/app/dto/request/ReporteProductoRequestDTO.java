package com.gestoralmacen.app.dto.request;

public class ReporteProductoRequestDTO {
    private Long empresaId;
    private Long categoriaId;
    private String estadoProducto;
    private Boolean incluirProductosVencidos;
    private Boolean incluirStockMinimo;

    public ReporteProductoRequestDTO() {
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public Boolean getIncluirProductosVencidos() {
        return incluirProductosVencidos;
    }

    public void setIncluirProductosVencidos(Boolean incluirProductosVencidos) {
        this.incluirProductosVencidos = incluirProductosVencidos;
    }

    public Boolean getIncluirStockMinimo() {
        return incluirStockMinimo;
    }

    public void setIncluirStockMinimo(Boolean incluirStockMinimo) {
        this.incluirStockMinimo = incluirStockMinimo;
    }
}
