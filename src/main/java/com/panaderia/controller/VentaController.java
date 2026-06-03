package com.panaderia.controller;

import com.panaderia.model.*;
import com.panaderia.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService,
                           ClienteService clienteService,
                           ProductoService productoService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Venta venta = new Venta();
        venta.setDetalles(new java.util.ArrayList<>());
        venta.getDetalles().add(new DetalleVenta());

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("estados", EstadoVenta.values());
        return "ventas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Venta venta, Model model) {
        try {
            ventaService.guardar(venta);
        } catch (Exception e) {
            model.addAttribute("clientes", clienteService.listar());
            model.addAttribute("productos", productoService.listar());
            model.addAttribute("estados", EstadoVenta.values());
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "ventas/form";
        }
        return "redirect:/ventas";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoVenta estado) {
        ventaService.actualizarEstado(id, estado);
        return "redirect:/ventas";
    }
}