package com.example.uninaswap.entity;

public class Recensione {

    private int id;
    private int voto;
    private String commento;
    private String recensito;
    private String recensore;

    // --- COSTRUTTORI ---
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

    // --- GETTER E SETTER ORIGINALI (NON TOCCARLI) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public String getRecensito() { return recensito; }
    public void setRecensito(String recensito) { this.recensito = recensito; }

    public String getRecensore() { return recensore; }
    public void setRecensore(String recensore) { this.recensore = recensore; }


    // --- ALIAS PER LA BOUNDARY (COSI' FUNZIONA LA SCHERMATA RECENSIONI) ---

    public String getEmailRecensito() {
        return recensito;
    }

    public void setEmailRecensito(String email) {
        this.recensito = email;
    }

    public String getEmailRecensore() {
        return recensore;
    }

    public void setEmailRecensore(String email) {
        this.recensore = email;
    }

    // --- TO STRING ---
    @Override
    public String toString() {
        return "Recensione di: " + recensore + " dedicata a " + recensito +
                "\nCommento: " + commento + "\nVoto: " + voto;
    }
}