package com.panaderia.service;

import com.panaderia.model.*;
import com.panaderia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaService(VentaRepository ventaRepository,
                        ProductoRepository productoRepository,
                        ClienteRepository clienteRepository,
                        DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.detalleVentaRepository = detalleVentaRepository;
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

        List<DetalleVenta> detalles = venta.getDetalles();
        venta.setDetalles(new java.util.ArrayList<>());

        Venta ventaGuardada = ventaRepository.saveAndFlush(venta);

        double total = 0;

        for (DetalleVenta detalle : detalles) {

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida para: " + producto.getNombre());
            }

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            double subtotal = producto.getPrecio() * detalle.getCantidad();

            DetalleVenta nuevoDetalle = new DetalleVenta();
            nuevoDetalle.setVenta(ventaGuardada);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(detalle.getCantidad());
            nuevoDetalle.setSubtotal(subtotal);

            detalleVentaRepository.save(nuevoDetalle);

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total += subtotal;
        }

        ventaGuardada.setTotal(total);
        return ventaRepository.save(ventaGuardada);
    }

    public Venta actualizarEstado(Long id, EstadoVenta nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado(nuevoEstado);
        return ventaRepository.save(venta);
    }
}