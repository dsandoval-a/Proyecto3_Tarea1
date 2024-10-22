package com.project.demo.rest.Producto;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import com.project.demo.logic.entity.producto.Producto;
import com.project.demo.logic.entity.producto.ProductoRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository; // Inyección del repositorio de categorías


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProductos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Producto> productosPage = productoRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productosPage.getTotalPages());
        meta.setTotalElements(productosPage.getTotalElements());
        meta.setPageNumber(productosPage.getNumber() + 1);
        meta.setPageSize(productosPage.getSize());

        return new GlobalResponseHandler().handleResponse("Productos obtenidos con éxito",
                productosPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createProducto(@RequestBody Producto producto, HttpServletRequest request) {
        // Imprimir el objeto Producto recibido
        System.out.println("Producto recibido: " + producto);

        // Verificar si la categoría ya existe
        if (producto.getCategoria() != null) {
            System.out.println("ID de categoría recibido: " + producto.getCategoria().getId());

            if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
                Categoria categoria = categoriaRepository.findById(producto.getCategoria().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
                producto.setCategoria(categoria);
            } else {
                // Maneja el caso donde no hay ID (puedes lanzar un error o manejarlo de otra forma)
                return new GlobalResponseHandler().handleResponse("ID de categoría no especificado", null, HttpStatus.BAD_REQUEST, request);
            }
        } else {
            return new GlobalResponseHandler().handleResponse("Categoría no especificada", null, HttpStatus.BAD_REQUEST, request);
        }

        // Guardar el producto
        Producto nuevoProducto = productoRepository.save(producto);
        return new GlobalResponseHandler().handleResponse("Producto creado con éxito",
                nuevoProducto, HttpStatus.CREATED, request);
    }




    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody Producto producto, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if (foundProducto.isPresent()) {
            Producto existingProducto = foundProducto.get();
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setDescripcion(producto.getDescripcion());
            existingProducto.setPrecio(producto.getPrecio());
            existingProducto.setCantidad_en_stock(producto.getCantidad_en_stock());
            existingProducto.setCategoria(producto.getCategoria());
            productoRepository.save(existingProducto);
            return new GlobalResponseHandler().handleResponse("Producto actualizado con éxito",
                    existingProducto, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto con id " + id + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(id);
        if (foundProducto.isPresent()) {
            productoRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Producto eliminado con éxito",
                    foundProducto.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto con id " + id + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
