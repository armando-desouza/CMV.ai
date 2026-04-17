package com.restaurant.cmv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/insumos")
public class InsumoController {
    
    @Autowired
    private InsumoService service;

    @GetMapping
    public List<Insumo> listarTodos() {
        return service.listarTodos();
    }
}
