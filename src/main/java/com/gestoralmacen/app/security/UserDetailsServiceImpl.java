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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos primero en la tabla de usuarios de las empresas
        Optional<Usuario> optUsuario = usuarioRepository.findByUsuario(username);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Controlar que una empresa suspendida no pueda ingresar
            if (usuario.getEmpresa() != null && !"ACTIVO".equalsIgnoreCase(usuario.getEmpresa().getEstado())) {
                throw new UsernameNotFoundException("La empresa del usuario se encuentra " + usuario.getEmpresa().getEstado() + ".");
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            return new User(
                    usuario.getUsuario(),
                    usuario.getContrasena(),
                    usuario.getActivo(),
                    true,
                    true,
                    true,
                    Collections.singletonList(authority));
        }

        // 2. Si no existe, buscamos en la tabla de administradores SaaS
        Administrador admin = administradorRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o Administrador no encontrado: " + username));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMINISTRADOR");
        return new User(
                admin.getUsuario(),
                admin.getPassword(),
                admin.getActivo(),
                true,
                true,
                true,
                Collections.singletonList(authority));
    }
}