package com.example.tcc_sgd;

import com.google.android.libraries.places.api.model.Place;

import java.lang.reflect.Type;
import java.util.List;

public class Estabelecimento {
    private String nome, endereco;
    private String movimento;
    private String tipoEstabelecimento;
    private String hora;

    public Estabelecimento(String nome, String endereco, String hora) {
        this.nome = nome;
        this.endereco = endereco;
        this.hora = hora;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) { this.hora = hora; }

}
