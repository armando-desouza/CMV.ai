package com.restaurant.cmv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class InsumoService {
    @Autowired
    private InsumoRepository repository;

    public List<Insumo> listarTodos() {
        return repository.findAll();
    }

    public boolean analisarRiscoDePreco(Long idInsumo) {
        Optional<Insumo> caixa = repository.findById(idInsumo);

        if (caixa.isEmpty()) {
            System.out.println("❌ Erro: Insumo não encontrado no sistema!");
            return false;
        }

        Insumo insumo = caixa.get();
        BigDecimal atual = insumo.getPrecoAtual();
        BigDecimal anterior = insumo.getPrecoAnterior();
        BigDecimal limiteRisco = insumo.getMargemRisco();

        BigDecimal diference = atual.subtract(anterior);

        if (diference.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("✅ O preço baixou ou se manteve igual.");
            return false;
        }

        BigDecimal percentualAumento = diference.divide(anterior, 4, RoundingMode.HALF_UP);
        
        if (percentualAumento.compareTo(limiteRisco) >= 0) {
            System.out.println("⚠️ ALERTA: O " + insumo.getNome() + " subiu demais! Aumento de " + percentualAumento);
            return true;
            // Chamada de LLM entrará aqui
        } else {
            System.out.println("✅ O preço subiu, mas está dentro da margem de segurança.");
            return false;
        }
    }
}
