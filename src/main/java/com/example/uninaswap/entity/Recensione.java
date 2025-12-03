package com.example.uninaswap.entity;

public class Recensione {

    private int id;

    private int voto;

    private String commento;

    private String recensito;

    private String recensore;

    public String getRecensito() {
        return recensito;
    }

    public String getRecensore() {
        return recensore;
    }

    public Recensione(String recensito, String recensore, int voto) {
        this.recensito = recensito;
        this.recensore = recensore;
        this.voto = voto;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public int getVoto() {
        return voto;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getCommento() {
        return commento;
    }

    @Override
    public String toString() {
        return "Recensione di :" + recensore + " dedicata a " + recensito + "\n" + commento + "\n" + voto;
    }

}
