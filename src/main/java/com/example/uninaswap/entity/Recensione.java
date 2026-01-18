package com.example.uninaswap.entity;

public class Recensione {

    //Attributi
    //
    private int id;
    private int voto;
    private String commento;
    private String recensito;
    private String recensore;

    //Costruttori
    //
    public Recensione(String recensito, String recensore, int voto) {
        this.recensito = recensito;
        this.recensore = recensore;
        this.voto = voto;
    }

    public Recensione(int id, String recensito, String recensore, int voto, String commento) {
        this.id = id;
        this.recensito = recensito;
        this.recensore = recensore;
        this.voto = voto;
        this.commento = commento;
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public String getEmailRecensito() {
        return recensito;
    }

    public String getEmailRecensore() {
        return recensore;
    }

    //toString
    //
    @Override
    public String toString() {
        return "Recensione di: " + recensore + " dedicata a " + recensito + "\nCommento: " + commento + "\nVoto: " + voto;
    }

}