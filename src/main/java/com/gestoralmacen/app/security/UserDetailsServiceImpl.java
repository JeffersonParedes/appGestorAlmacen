package com.gestoralmacen.app.security;

import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.entity.Usuario;
import com.gestoralmacen.app.repository.AdministradorRepository;
import com.gestoralmacen.app.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos primero en la tabla de usuarios de las empresas con su Empresa unida
        Optional<Usuario> optUsuario = usuarioRepository.findByUsuario(username);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Controlar que una empresa suspendida no pueda ingresar
            if (usuario.getEmpresa() != null && usuario.getEmpresa().getEstado() != null 
                    && !"ACTIVO".equalsIgnoreCase(usuario.getEmpresa().getEstado())) {
                throw new UsernameNotFoundException("La empresa del usuario se encuentra " + usuario.getEmpresa().getEstado() + ".");
            }

            String rol = usuario.getRol() != null ? usuario.getRol() : "EMPLEADO";
            String rolAuthority = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolAuthority);

            boolean activo = usuario.getActivo() != null ? usuario.getActivo() : true;

            return new User(
                    usuario.getUsuario(),
                    usuario.getContrasena(),
                    activo,
                    true,
                    true,
                    true,
                    Collections.singletonList(authority));
        }

        // 2. Si no existe, buscamos en la tabla de administradores SaaS
        Administrador admin = administradorRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o Administrador no encontrado: " + username));

        boolean adminActivo = admin.getActivo() != null ? admin.getActivo() : true;

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMINISTRADOR");
        return new User(
                admin.getUsuario(),
                admin.getPassword(),
                adminActivo,
                true,
                true,
                true,
                Collections.singletonList(authority));
    }
}