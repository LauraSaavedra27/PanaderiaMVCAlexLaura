package com.panaderia.service;

import com.panaderia.model.*;
import com.panaderia.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductoRepository productoRepository,
                        ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    public Venta guardar(Venta venta) {

        Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        venta.setCliente(cliente);
        venta.setFecha(java.time.LocalDate.now());

        double total = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida para: " + producto.getNombre());
            }

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            double subtotal = producto.getPrecio() * detalle.getCantidad();
            detalle.setProducto(producto);
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total += subtotal;
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }
}