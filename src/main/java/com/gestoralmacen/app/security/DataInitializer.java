package com.gestoralmacen.app.security;

import com.gestoralmacen.app.entity.Administrador;
import com.gestoralmacen.app.repository.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            AdministradorRepository administradorRepository,
            PasswordEncoder passwordEncoder) {

        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializar Administrador SaaS por defecto
        if (administradorRepository.findByUsuario("superadmin").isEmpty()) {
            Administrador superadmin = new Administrador();
            superadmin.setUsuario("superadmin");
            superadmin.setPassword(passwordEncoder.encode("superpassword"));
            superadmin.setCorreo("superadmin@gestoralmacen.com");
            superadmin.setNombreCompleto("SaaS Administrator");
            superadmin.setActivo(true);

            administradorRepository.save(superadmin);
            System.out.println("✅ Administrador SAAS generado con éxito. Credenciales -> superadmin / superpassword");
        }
    }
}
