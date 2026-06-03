package com.panaderia.controller;

import com.panaderia.service.ClienteService;
import com.panaderia.service.ProductoService;
import com.panaderia.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final VentaService ventaService;

    public HomeController(ClienteService clienteService,
                          ProductoService productoService,
                          VentaService ventaService) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalProductos", productoService.listar().size());
        model.addAttribute("totalVentas", ventaService.listar().size());

        double totalDinero = ventaService.listar()
                .stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0)
                .sum();

        model.addAttribute("totalDinero", totalDinero);

        return "index";
    }
}
