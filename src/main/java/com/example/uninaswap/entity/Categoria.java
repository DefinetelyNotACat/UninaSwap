package com.example.uninaswap.entity;


public class Categoria {

    //Attributi
    //
    private String nome;

    //Costruttori
    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria() {}

    //Getter e Setter
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Categoria categoria = (Categoria) object;
        //serve per fare in modo tale che due categorie dello stesso nome siano considerate uguali
        return nome != null ? nome.equals(categoria.nome) : categoria.nome == null;
    }

    //toString
    //
    @Override
    public String toString() {
        return "Nome: " + nome;
    }

}