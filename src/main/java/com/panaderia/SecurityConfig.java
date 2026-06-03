package com.panaderia;

import com.panaderia.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final PasswordConfig passwordConfig;

    public SecurityConfig(UsuarioService usuarioService, PasswordConfig passwordConfig) {
        this.usuarioService = usuarioService;
        this.passwordConfig = passwordConfig;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioService);
        provider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**").permitAll()

                        // Solo ADMIN puede gestionar usuarios y categorías
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/categorias/**").hasRole("ADMIN")

                        // Solo ADMIN puede eliminar clientes y productos
                        .requestMatchers("/clientes/eliminar/**").hasRole("ADMIN")
                        .requestMatchers("/productos/eliminar/**").hasRole("ADMIN")

                        // Solo ADMIN puede crear/editar productos
                        .requestMatchers("/productos/nuevo", "/productos/guardar", "/productos/editar/**").hasRole("ADMIN")

                        // Todo lo demás requiere estar autenticado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}