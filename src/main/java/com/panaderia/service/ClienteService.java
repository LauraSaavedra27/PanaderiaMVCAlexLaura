package com.panaderia.service;

import com.panaderia.model.Cliente;
import com.panaderia.model.Venta;
import com.panaderia.repository.ClienteRepository;
import com.panaderia.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        List<Venta> ventas = ventaRepository.findByClienteId(id);
        if (!ventas.isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el cliente tiene ventas registradas.");
        }
        clienteRepository.deleteById(id);
    }
}