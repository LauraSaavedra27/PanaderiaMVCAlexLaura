package com.panaderia.controller;

import com.panaderia.model.Cliente;
import com.panaderia.model.Venta;
import com.panaderia.repository.VentaRepository;
import com.panaderia.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final VentaRepository ventaRepository;

    public ClienteController(ClienteService clienteService,
                             VentaRepository ventaRepository) {
        this.clienteService = clienteService;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente) {
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "clientes/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/historial")
    public String historial(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        List<Venta> ventas = ventaRepository.findByClienteId(id);
        double totalGeneral = ventas.stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0)
                .sum();
        model.addAttribute("cliente", cliente);
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalGeneral", totalGeneral);
        return "clientes/historial";
    }
}