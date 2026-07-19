package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.dto.request.AlmacenRequestDTO;
import com.gestoralmacen.app.dto.response.AlmacenResponseDTO;
import com.gestoralmacen.app.entity.Almacen;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.exception.RecursoNoEncontradoException;
import com.gestoralmacen.app.exception.ReglaNegocioException;
import com.gestoralmacen.app.mapper.AlmacenMapper;
import com.gestoralmacen.app.repository.AlmacenRepository;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.service.AlmacenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlmacenServiceImpl implements AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final EmpresaRepository empresaRepository;
    private final AlmacenMapper almacenMapper;

    public AlmacenServiceImpl(AlmacenRepository almacenRepository, EmpresaRepository empresaRepository,
            AlmacenMapper almacenMapper) {
        this.almacenRepository = almacenRepository;
        this.empresaRepository = empresaRepository;
        this.almacenMapper = almacenMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlmacenResponseDTO> listarPorEmpresa(Long empresaId) {
        return almacenRepository.findByEmpresaId(empresaId).stream()
                .filter(almacen -> !almacen.getEstado().equals("INACTIVO")) // Filtramos los inactivos manualmente si no
                                                                            // hay un findByEmpresaIdAndEstado
                .map(almacenMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlmacenResponseDTO crearAlmacen(AlmacenRequestDTO requestDTO, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        Almacen nuevoAlmacen = almacenMapper.toEntity(requestDTO);
        nuevoAlmacen.setEmpresa(empresa);

        Almacen guardado = almacenRepository.save(nuevoAlmacen);
        return almacenMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminarAlmacen(Long id, Long empresaId) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado"));

        if (!almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este almacén.");
        }

        almacen.setEstado("INACTIVO");
        almacenRepository.save(almacen);
    }

    @Override
    @Transactional
    public AlmacenResponseDTO actualizarAlmacen(Long id, AlmacenRequestDTO requestDTO, Long empresaId) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado con ID: " + id));

        if (!almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este almacén.");
        }

        almacen.setNombre(requestDTO.getNombre());
        almacen.setDireccion(requestDTO.getDireccion());

        Almacen guardado = almacenRepository.save(almacen);
        return almacenMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public void activarAlmacen(Long id, Long empresaId) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado con ID: " + id));

        if (!almacen.getEmpresa().getId().equals(empresaId)) {
            throw new ReglaNegocioException("No tienes permiso sobre este almacén.");
        }

        almacen.setEstado("ACTIVO");
        almacenRepository.save(almacen);
    }

    @Override
    @Transactional
    public void desactivarAlmacen(Long id, Long empresaId) {
        eliminarAlmacen(id, empresaId);
    }
}

