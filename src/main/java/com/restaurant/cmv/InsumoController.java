package com.restaurant.cmv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/insumos")
@CrossOrigin(origins = "*")
public class InsumoController {
    
    @Autowired
    private InsumoService service;

    @GetMapping
    public List<Insumo> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public Insumo cadastrarNovo(@RequestBody Insumo novoInsumo) {
        return service.salvar(novoInsumo);
    }

    @PutMapping("/{id}")
    public Insumo atualizarInsumo(@PathVariable Long id, @RequestBody Insumo insumoAtualizado) {
        return service.atualizar(id, insumoAtualizado);
    }

    @DeleteMapping("/{id}")
    public void excluirInsumo(@PathVariable Long id) {
        service.excluir(id);
    }

    @GetMapping("/{id}/prompt_llm")
    public String pedirSugestaoIA(@PathVariable Long id) {
        return service.analisarRiscoDePreco(id);
    }
}
