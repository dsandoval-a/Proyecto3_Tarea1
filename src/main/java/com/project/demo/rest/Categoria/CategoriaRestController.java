package com.project.demo.rest.Categoria;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/categorias")
public class CategoriaRestController {

    @Autowired
    private CategoriaRepository CategoriaRepository;


        @GetMapping
        @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
        public List<Categoria> getAllProductos() {
            return CategoriaRepository.findAll();
        }

        @PostMapping
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        public Categoria createCategoria(@RequestBody Categoria categoria) {
            return CategoriaRepository.save(categoria);
        }

        @PutMapping("/{id}")
        public Categoria updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
            return CategoriaRepository.findById(id)
                    .map(existingCategoria -> {
                        existingCategoria.setNombre(categoria.getNombre());
                        existingCategoria.setDescripcion(categoria.getDescripcion());

                        return CategoriaRepository.save(existingCategoria);
                    })
                    .orElseGet(() -> {
                        categoria.setId(id);
                        return CategoriaRepository.save(categoria);
                    });
        }



        @DeleteMapping("/{id}")
        public void deleteCategoria(@PathVariable Long id) {
            CategoriaRepository.deleteById(id);
        }


}
