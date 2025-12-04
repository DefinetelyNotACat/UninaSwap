package com.example.uninaswap.entity;

import java.util.ArrayList;
import java.util.Date;

public class Immagine {

    //Attributi
    //
    private int id;
    private Date dataCaricamento;
    private String path;
    private Oggetto oggetto;

    //Costruttori
    //
    public Immagine(Date dataCaricamento, String path, Oggetto oggetto) {
        this.dataCaricamento = dataCaricamento;
        this.path = path;
        this.oggetto = oggetto;
    }

    //Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataCaricamento() {
        return dataCaricamento;
    }

    public void setDataCaricamento(Date dataCaricamento) {
        this.dataCaricamento = dataCaricamento;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Oggetto getOggetto() {
        return oggetto;
    }

    public void setOggetto(Oggetto oggetto) {
        this.oggetto = oggetto;
    }

    //toString
    //
    @Override
    public String toString() {
        return "Id: " + this.id + " Data Caricamento: " + this.dataCaricamento + " Path: " + this.path + " Oggetto: " + this.oggetto.toString();
    }
}
