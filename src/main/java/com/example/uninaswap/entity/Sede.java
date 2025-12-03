package com.example.uninaswap.entity;

import java.util.ArrayList;

public class Sede {
    private int id;
    private String nomeSede;
    private String indirizzo;
    private ArrayList<Annuncio> annunci = new ArrayList<Annuncio>();

    public Sede(String nomeSede, String indirizzo) {
        this.nomeSede = nomeSede;
        this.indirizzo = indirizzo;
    }

    public void aggiungiAnnuncio(Annuncio annuncio) {
        if (this.annunci != null) {
            this.annunci.add(annuncio);
        }
    }

    public ArrayList<Annuncio> getAnnunci() {
        return annunci;
    }

    public void rimuoviAnnunci() {
        this.annunci.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeSede() {
        return nomeSede;
    }
    public void setNomeSede(String nomeSede) {
        this.nomeSede = nomeSede;
    }
    public String getIndirizzo() {
        return indirizzo;
    }
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}
