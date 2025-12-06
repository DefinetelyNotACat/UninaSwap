package com.example.uninaswap.entity;
import java.util.ArrayList;

public class Categoria {

    //Attributi
    //
    private String nome;
    ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();

    //Costruttori
    //
    public Categoria(String nome) {
        this.nome = nome;
    }

    //Adder, Remover, Clearer
    //
    public void aggiungiOggetto(Oggetto oggetto) {
        this.oggetti.add(oggetto);
        oggetto.addCategoria(this);
    }

    //Getter e Setter
    //
    public String getNome() {
        return nome;
    }

    public void setNome(String Nome) {
        this.nome = nome;
    }

    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }

    //toString
    //
    @Override
    public String toString() {
        return nome;
    }
}
