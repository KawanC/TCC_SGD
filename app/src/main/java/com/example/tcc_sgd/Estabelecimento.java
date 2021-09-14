package com.example.tcc_sgd;

public class Estabelecimento {
    private String nome;
    private int movimento;
    private String tamanhoEstabelecimento;
    private String hora;

    public Estabelecimento(String nome, int movimento, String tamanhoEstabelecimento, String hora) {
        this.nome = nome;
        this.movimento = movimento;
        this.tamanhoEstabelecimento = tamanhoEstabelecimento;
        this.hora = hora;
    }

    public String getNome() {
        return nome;
    }

    public int getMovimento() {
        return movimento;
    }

    public String getTamanhoEstabelecimento() {
        return tamanhoEstabelecimento;
    }

    public String getHora() {
        return hora;
    }
}
