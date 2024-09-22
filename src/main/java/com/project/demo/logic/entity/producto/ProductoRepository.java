package com.project.demo.logic.entity.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProductoRepository  extends JpaRepository<Producto, Long> {}
