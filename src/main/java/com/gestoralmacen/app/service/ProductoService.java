package com.gestoralmacen.app.service;

import com.gestoralmacen.app.dto.request.ProductoRequestDTO;
import com.gestoralmacen.app.dto.response.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> listarActivosPorEmpresa(Long empresaId);

    List<ProductoResponseDTO> listarTodosPorEmpresa(Long empresaId);

    List<ProductoResponseDTO> listarAprobadosPorEmpresa(Long empresaId);

    // Para la "Papelera" de la que hablamos
    List<ProductoResponseDTO> listarBorradosPorEmpresa(Long empresaId);

    // Sugerida por el empleado (Entra como PENDIENTE) o creada directamente por Bodeguero (Entra como APROBADO)
    ProductoResponseDTO sugerirProducto(ProductoRequestDTO requestDTO, Long empresaId);

    ProductoResponseDTO registrarProducto(ProductoRequestDTO requestDTO, Long empresaId, Long usuarioId);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO, Long empresaId);

    ProductoResponseDTO obtenerPorId(Long id, Long empresaId);

    // Aprobada por el dueño
    void aprobarProducto(Long id, Long empresaId);

    // Rechazada por el dueño
    void rechazarProducto(Long id, Long empresaId);

    // Borrado Lógico
    void eliminarProducto(Long id, Long empresaId);

    // Consultas específicas
    List<ProductoResponseDTO> consultarProductosConStockMinimo(Long empresaId);

    List<ProductoResponseDTO> consultarProductosProximosAVencer(Long empresaId, int diasThreshold);
}