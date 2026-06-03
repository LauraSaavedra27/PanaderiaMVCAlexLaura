package com.panaderia.service;

import com.panaderia.model.*;
import com.panaderia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public Venta guardar(Venta venta) {

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("Debes agregar al menos un producto a la venta");
        }

        Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        venta.setCliente(cliente);
        venta.setFecha(java.time.LocalDate.now());

        if (venta.getEstado() == null) {
            venta.setEstado(EstadoVenta.PENDIENTE);
        }

        double total = 0;
        List<DetalleVenta> detallesValidos = new ArrayList<>();

        for (DetalleVenta detalle : venta.getDetalles()) {

            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                continue;
            }

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
            detalle.setVenta(venta); // ← clave: apunta al objeto venta antes de guardar

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total += subtotal;
            detallesValidos.add(detalle);
        }

        if (detallesValidos.isEmpty()) {
            throw new RuntimeException("Debes agregar al menos un producto válido");
        }

        venta.setDetalles(detallesValidos);
        venta.setTotal(total);

        return ventaRepository.save(venta); // cascade guarda los detalles automáticamente
    }

    public Venta actualizarEstado(Long id, EstadoVenta nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado(nuevoEstado);
        return ventaRepository.save(venta);
    }
}