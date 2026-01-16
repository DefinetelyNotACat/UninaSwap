package com.example.uninaswap.entity;


public class Categoria {

    private String nome;

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    // Equals e HashCode sono importanti per far funzionare bene le ArrayList
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return nome != null ? nome.equals(categoria.nome) : categoria.nome == null;
    }


}