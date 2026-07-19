package com.gestoralmacen.app.service.impl;

import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.entity.Auditoria;
import com.gestoralmacen.app.entity.Empresa;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.repository.AdministradorRepository;
import com.gestoralmacen.app.repository.AuditoriaRepository;
import com.gestoralmacen.app.repository.EmpresaRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import com.gestoralmacen.app.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final EmpresaRepository empresaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository,
                                UsuarioRepository usuarioRepository,
                                AdministradorRepository administradorRepository,
                                EmpresaRepository empresaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
        this.empresaRepository = empresaRepository;
    }

    @Override
    @Transactional
    public void registrar(Long usuarioId, Long administradorId, Long empresaId, String accion, String tablaAfectada, Long registroId, String descripcion, String ip) {
        Auditoria auditoria = new Auditoria();
        
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            auditoria.setUsuario(usuario);
        }
        if (administradorId != null) {
            Administrador admin = administradorRepository.findById(administradorId).orElse(null);
            auditoria.setAdministrador(admin);
        }
        if (empresaId != null) {
            Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
            auditoria.setEmpresa(empresa);
        }

        auditoria.setAccion(accion);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setRegistroId(registroId);
        auditoria.setDescripcion(descripcion);
        auditoria.setIp(ip);

        auditoriaRepository.save(auditoria);
    }
}
