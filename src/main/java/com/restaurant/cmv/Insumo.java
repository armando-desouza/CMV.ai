package com.restaurant.cmv;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "insumos")
public class Insumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "precoatual")
    private BigDecimal precoAtual;

    @Column(name = "precoanterior")
    private BigDecimal precoAnterior;

    @Column(name = "unidademedida")
    private String unidadeMedida;

    @Column(name = "margem_risco")
    private BigDecimal margemRisco;

    @Column(name = "margem_ganho")
    private BigDecimal margemGanho;

    @Column(name = "escassez_mercado")
    private Boolean escassezMercado;

    @Column(name = "prazo_chegada_dias")
    private Integer prazoChegadaDias;

    public Insumo() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id; 
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }
    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }

    public BigDecimal getPrecoAnterior() {
        return precoAnterior;
    }
    public void setPrecoAnterior(BigDecimal precoAnterior) {
        this.precoAnterior = precoAnterior;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }
    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getMargemRisco() {
        return margemRisco;
    }
    public void setMargemRisco(BigDecimal margemRisco) {
        this.margemRisco = margemRisco;
    }

    public BigDecimal getMargemGanho() {
        return margemGanho;
    }
    public void setMargemGanho(BigDecimal margemGanho) {
        this.margemGanho = margemGanho;
    }

    public Boolean getEscassezMercado() {
        return escassezMercado;
    }
    public void setEscassezMercado(Boolean escassezMercado) {
        this.escassezMercado = escassezMercado;
    }
}
