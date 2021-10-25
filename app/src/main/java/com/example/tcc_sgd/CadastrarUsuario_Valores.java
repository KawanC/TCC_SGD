package com.example.tcc_sgd;

public class CadastrarUsuario_Valores {
    private String nome, email, senha, telefone, data_nasc;

    public CadastrarUsuario_Valores(String nome , String email, String data_nasc, String telefone) {
        this.nome = nome;
        this.email = email;
        this.data_nasc = data_nasc;
        this.telefone = telefone;

    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getData_nasc() {
        return data_nasc;
    }

}
