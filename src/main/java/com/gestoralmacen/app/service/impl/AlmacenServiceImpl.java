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
                .filter(almacen -> !"INACTIVO".equalsIgnoreCase(almacen.getEstado()))
                .map(almacenMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlmacenResponseDTO crearAlmacen(AlmacenRequestDTO requestDTO, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empresa no encontrada"));

        String nombre = requestDTO.getNombre() != null ? requestDTO.getNombre().trim() : "";
        String direccion = requestDTO.getDireccion() != null ? requestDTO.getDireccion().trim() : "";

        if (almacenRepository.existsByEmpresaIdAndNombreIgnoreCase(empresaId, nombre)) {
            throw new ReglaNegocioException("El nombre del almacén ya se encuentra registrado.");
        }

        if (!direccion.isEmpty() && almacenRepository.existsByEmpresaIdAndDireccionIgnoreCase(empresaId, direccion)) {
            throw new ReglaNegocioException("La dirección del almacén ya se encuentra registrada.");
        }

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

        String nombre = requestDTO.getNombre() != null ? requestDTO.getNombre().trim() : "";
        String direccion = requestDTO.getDireccion() != null ? requestDTO.getDireccion().trim() : "";

        if (almacenRepository.existsByEmpresaIdAndNombreIgnoreCaseAndIdNot(empresaId, nombre, id)) {
            throw new ReglaNegocioException("El nombre del almacén ya se encuentra registrado.");
        }

        if (!direccion.isEmpty() && almacenRepository.existsByEmpresaIdAndDireccionIgnoreCaseAndIdNot(empresaId, direccion, id)) {
            throw new ReglaNegocioException("La dirección del almacén ya se encuentra registrada.");
        }

        almacen.setNombre(nombre);
        almacen.setDireccion(direccion);

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
