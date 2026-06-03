package com.panaderia.service;

import com.panaderia.model.Categoria;
import com.panaderia.model.Producto;
import com.panaderia.repository.CategoriaRepository;
import com.panaderia.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        List<Producto> productos = productoRepository.findAll()
                .stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().getId().equals(id))
                .toList();

        if (!productos.isEmpty()) {
            throw new RuntimeException("No se puede eliminar: la categoría tiene productos asociados.");
        }
        categoriaRepository.deleteById(id);
    }
}