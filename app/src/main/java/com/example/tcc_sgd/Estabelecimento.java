package com.example.tcc_sgd;

import com.google.android.libraries.places.api.model.Place;

import java.lang.reflect.Type;
import java.util.List;

public class Estabelecimento {
    private String nome, endereco, telefone;
    private int movimento;
    private String tamanhoEstabelecimento;
    public String tipoEstabelecimento;
    private String hora;

    public Estabelecimento(String nome, String endereco, String telefone, int movimento, String tamanhoEstabelecimento, String tipoEstabelecimento, String hora) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.movimento = movimento;
        this.tamanhoEstabelecimento = tamanhoEstabelecimento;
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.hora = hora;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
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

    public void setHora(String hora) { this.hora = hora; }

}
