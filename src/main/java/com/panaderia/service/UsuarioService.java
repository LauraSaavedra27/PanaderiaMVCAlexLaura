package com.panaderia.service;

import com.panaderia.PasswordConfig;
import com.panaderia.model.Usuario;
import com.panaderia.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordConfig passwordConfig;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordConfig passwordConfig) {
        this.usuarioRepository = usuarioRepository;
        this.passwordConfig = passwordConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().replace("ROLE_", ""))
                .build();
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public void guardar(Usuario usuario) {
        usuario.setPassword(passwordConfig.passwordEncoder().encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}