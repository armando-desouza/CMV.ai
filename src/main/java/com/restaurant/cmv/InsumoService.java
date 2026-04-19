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

    @Value("${openai.api.key}")
    private String openaiApiKey;

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
            
            Map<String, String> mensagemUser = new HashMap<>();
            mensagemUser.put("role", "user");
            mensagemUser.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", 0.7);
            requestBody.put("messages", List.of(mensagemUser));

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();            headers.set("Authorization", "Bearer " + openaiApiKey);
            headers.set("Content-Type", "application/json");

            org.springframework.http.HttpEntity<Map<String, Object>> envelope = new org.springframework.http.HttpEntity<>(requestBody, headers);
            try {
                RestTemplate correio = new RestTemplate();
                String url = "https://api.openai.com/v1/chat/completions";

                System.out.println("📡 Enviando para OpenAI...");
                Map<String, Object> respostaBruta = correio.postForObject(url, envelope, Map.class);
                
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respostaBruta.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String textoDaIA = (String) message.get("content");

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
