package com.project.demo.rest.Producto;

import com.project.demo.logic.entity.producto.Producto;
import com.project.demo.logic.entity.producto.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")

public class ProductoRestController {

    @Autowired
    private ProductoRepository ProductoRepository;


    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public List<Producto> getAllProductos() {
        return ProductoRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Producto createProducto(@RequestBody Producto producto) {
        return ProductoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return ProductoRepository.findById(id)
                .map(existingProducto -> {
                    existingProducto.setNombre(producto.getNombre());
                    existingProducto.setDescripcion(producto.getDescripcion());
                    existingProducto.setPrecio(producto.getPrecio());
                    existingProducto.setCantidad_en_stock(producto.getCantidad_en_stock());
                    existingProducto.setCategoria(producto.getCategoria());
                    return ProductoRepository.save(existingProducto);
                })
                .orElseGet(() -> {
                    producto.setId(id);
                    return ProductoRepository.save(producto);
                });
    }



    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable Long id) {
        ProductoRepository.deleteById(id);
    }

}
