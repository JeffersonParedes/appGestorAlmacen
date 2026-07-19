package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.LoteRequestDTO;
import com.gestoralmacen.app.dto.response.LoteResponseDTO;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Lote;
import com.gestoralmacen.app.entity.Producto;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.LoteMapper;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.LoteRepository;
import com.gestoralmacen.app.repository.ProductoRepository;
import com.gestoralmacen.app.service.LoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteServiceImpl implements LoteService {

    private final LoteRepository loteRepository;
    private final LoteMapper loteMapper;
    private final EmpresaRepository empresaRepository;
    private final ProductoRepository productoRepository;

    public LoteServiceImpl(LoteRepository loteRepository,
                           LoteMapper loteMapper,
                           EmpresaRepository empresaRepository,
                           ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.loteMapper = loteMapper;
        this.empresaRepository = empresaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public LoteResponseDTO crearLote(LoteRequestDTO dto, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("El producto no pertenece a esta empresa.");
        }

        if (loteRepository.findByProductoIdAndNumeroLote(producto.getId(), dto.getNumeroLote()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un lote con número: " + dto.getNumeroLote() + " para este producto.");
        }

        Lote lote = loteMapper.toEntity(dto);
        lote.setEmpresa(empresa);
        lote.setProducto(producto);

        Lote guardado = loteRepository.save(lote);
        return loteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public LoteResponseDTO obtenerLotePorId(Long id, Long empresaId) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado con ID: " + id));

        if (!lote.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este lote.");
        }

        return loteMapper.toResponse(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> listarLotesPorProducto(Long productoId, Long empresaId) {
        return loteRepository.findByProductoId(productoId).stream()
                .filter(l -> l.getEmpresa().getId().equals(empresaId))
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponseDTO> consultarLotesPorVencer(Long empresaId, int diasThreshold) {
        LocalDate limitDate = LocalDate.now().plusDays(diasThreshold);
        return loteRepository.findByEmpresaId(empresaId).stream()
                .filter(l -> l.getFechaVencimiento() != null && 
                             !l.getFechaVencimiento().isBefore(LocalDate.now()) && 
                             l.getFechaVencimiento().isBefore(limitDate))
                .map(loteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
