package com.restaurant.cmv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InsumoServiceTest {
    
    @Mock 
    private InsumoRepository repository;

    @InjectMocks
    private InsumoService service;

    @Test
    public void deveDetectarAumentoAcimaDaMargem() {
        Insumo tomateFalso = new Insumo();
        tomateFalso.setId(1L);
        tomateFalso.setNome("Tomate Teste");
        tomateFalso.setPrecoAnterior(new BigDecimal("10.00"));
        tomateFalso.setPrecoAtual(new BigDecimal("15.00"));
        tomateFalso.setMargemRisco(new BigDecimal("0.20"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(tomateFalso));
        
        boolean resultado = service.analisarRiscoDePreco(1L);

        Assertions.assertTrue(resultado, "O sistema deveria ter retornado true para um aumento de 50%!");
    }
}
