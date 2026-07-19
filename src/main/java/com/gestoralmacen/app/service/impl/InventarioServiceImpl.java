package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.response.InventarioResponseDTO;
import com.gestoralmacen.app.entity.Inventario;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.mapper.InventarioMapper;
import com.gestoralmacen.app.repository.InventarioRepository;
import com.gestoralmacen.app.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;

    public InventarioServiceImpl(InventarioRepository inventarioRepository, InventarioMapper inventarioMapper) {
        this.inventarioRepository = inventarioRepository;
        this.inventarioMapper = inventarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarPorEmpresa(Long empresaId) {
        return inventarioRepository.findByEmpresaId(empresaId).stream()
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarPorAlmacen(Long almacenId, Long empresaId) {
        return inventarioRepository.findByAlmacenId(almacenId).stream()
                .filter(i -> i.getEmpresa().getId().equals(empresaId))
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerPorProductoYAlmacen(Long productoId, Long almacenId, Long empresaId) {
        Inventario inventario = inventarioRepository.findByEmpresaIdAndProductoIdAndAlmacenId(empresaId, productoId, almacenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró registro de inventario para el producto y almacén especificados."));
        return inventarioMapper.toResponse(inventario);
    }
}
