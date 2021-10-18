package com.example.tcc_sgd;

public class Model {
    private int imagem;
    private String titulo;
    private String desc;

    public Model(int imagem, String titulo, String desc) {
        this.imagem = imagem;
        this.titulo = titulo;
        this.desc = desc;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
