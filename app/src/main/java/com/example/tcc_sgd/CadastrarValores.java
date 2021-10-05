package com.example.tcc_sgd;

public class CadastrarValores {
    private String nome, email, senha;

    public CadastrarValores( String email, String senha, String nome) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }


}
