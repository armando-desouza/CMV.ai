package com.restaurant.cmv;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InsumoService {
    @Autowired
    private InsumoRepository repository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public List<Insumo> listarTodos() {
        return repository.findAll();
    }

    public String analisarRiscoDePreco(Long idInsumo) {
        Optional<Insumo> caixa = repository.findById(idInsumo);

        if (caixa.isEmpty()) {
            return "❌ Erro: Insumo não encontrado no sistema!";
        }

        Insumo insumo = caixa.get();
        BigDecimal atual = insumo.getPrecoAtual();
        BigDecimal anterior = insumo.getPrecoAnterior();
        BigDecimal limiteRisco = insumo.getMargemRisco();

        BigDecimal diference = atual.subtract(anterior);

        if (diference.compareTo(BigDecimal.ZERO) <= 0) {
            return "✅ O preço baixou ou se manteve igual.";
        }

        BigDecimal percentualAumento = diference.divide(anterior, 4, RoundingMode.HALF_UP);
        
        if (percentualAumento.compareTo(limiteRisco) >= 0) {
            System.out.println("⚠️ ALERTA: O " + insumo.getNome() + " subiu demais! Aumento de " + percentualAumento);

            String prompt = "Alerta de custo! O preço do(a) " + insumo.getNome() + " passou de R$ " + anterior + " para R$ " + atual + ". Por favor sugira um novo preço para os pratos e 2 dias de ingredientes substitutos para não repassar o custo ao cliente.";
            System.out.println("🤖 Prompt montado: " + prompt);
            
            Map<String, String> textMap = new HashMap();
            textMap.put("text", prompt);

            List<Map<String, String>> partsList = List.of(textMap);

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("parts", partsList);

            List<Map<String, Object>> contentsList = List.of(contentMap);

            Map<String, Object> requestBody = new HashMap();
            requestBody.put("contents", contentsList);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            org.springframework.http.HttpEntity<Map<String, Object>> envelope = new org.springframework.http.HttpEntity<>(requestBody, headers);

            try {
                RestTemplate correio = new RestTemplate();
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

                System.out.println("📡 Enviando para Gemini...");
                Map<String, Object> respostaBruta = correio.postForObject(url, envelope, Map.class);
                
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) respostaBruta.get("candidates");

                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");

                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                String textoDaIA = (String) parts.get(0).get("text");

                System.out.println("🤖 Sugestão da IA recebida!");
                return textoDaIA;

            } catch (Exception e) {
                System.out.println("❌ ERRO NA CHAMADA DA IA: " + e.getMessage());
                e.printStackTrace(); // Isso vai mostrar o erro detalhado no terminal
                return "Erro ao falar com a IA: " + e.getMessage();
            }
        } else {
            System.out.println("✅ O preço subiu, mas está dentro da margem de segurança.");
            return "O preço do " + insumo.getNome() + " está sob controle.";
        }
    }

    public Insumo salvar(Insumo novoInsumo) {
        return repository.save(novoInsumo);
    }

    public Insumo atualizar(Long id, Insumo insumoAtualizado) {
        Optional<Insumo> caixa = repository.findById(id);

        if (caixa.isPresent()) {
            Insumo insumoExistente = caixa.get();

            insumoExistente.setNome(insumoAtualizado.getNome());
            insumoExistente.setPrecoAnterior(insumoAtualizado.getPrecoAnterior());
            insumoExistente.setPrecoAtual(insumoAtualizado.getPrecoAtual());
            insumoExistente.setUnidadeMedida(insumoAtualizado.getUnidadeMedida());
            insumoExistente.setMargemRisco(insumoAtualizado.getMargemRisco());
            insumoExistente.setMargemGanho(insumoAtualizado.getMargemGanho());
            insumoExistente.setEscassezMercado(insumoAtualizado.getEscassezMercado());
            insumoExistente.setPrazoChegadaDias(insumoAtualizado.getPrazoChegadaDias());

            return repository.save(insumoExistente);
        }

        return null;
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
