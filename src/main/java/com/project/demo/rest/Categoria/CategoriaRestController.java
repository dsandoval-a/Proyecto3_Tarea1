package com.project.demo.rest.Categoria;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/categorias")
public class CategoriaRestController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategoria(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Categoria> categoriasPage = categoriaRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriasPage.getTotalPages());
        meta.setTotalElements(categoriasPage.getTotalElements());
        meta.setPageNumber(categoriasPage.getNumber() + 1);
        meta.setPageSize(categoriasPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categorias obtenidas con éxito",
                categoriasPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createCategoria(@RequestBody Categoria categoria, HttpServletRequest request) {
        Categoria nuevaCategoria = categoriaRepository.save(categoria);
        return new GlobalResponseHandler().handleResponse("Categoria creada con éxito",
                nuevaCategoria, HttpStatus.CREATED, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(id);
        if (foundCategoria.isPresent()) {
            Categoria existingCategoria = foundCategoria.get();
            existingCategoria.setNombre(categoria.getNombre());
            existingCategoria.setDescripcion(categoria.getDescripcion());
            categoriaRepository.save(existingCategoria);
            return new GlobalResponseHandler().handleResponse("Categoria actualizada con éxito",
                    existingCategoria, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria con id " + id + " no encontrada",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(id);
        if (foundCategoria.isPresent()) {
            categoriaRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Categoria eliminada con éxito",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria con id " + id + " no encontrada",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
