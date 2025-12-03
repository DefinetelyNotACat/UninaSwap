package com.example.uninaswap.entity;
import java.util.ArrayList;

public class Categoria {
    private String nome;

    ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();

    public Categoria(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String Nome) {
        this.nome = nome;
    }

    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }
    public void setOggetti(Oggetto oggetto) {
        this.oggetti.add(oggetto);
    }

}
