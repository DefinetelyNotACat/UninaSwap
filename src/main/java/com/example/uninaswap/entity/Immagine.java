package com.example.uninaswap.entity;

import com.example.uninaswap.interfaces.GestoreCondizioneDAO;
import com.example.uninaswap.interfaces.GestoreImmagineDAO;

import java.util.ArrayList;
import java.util.Date;

public class Immagine {

    //Attributi
    //
    private int id;
    private Date dataCaricamento;
    private String path;
    private int idOggetto;

    //Costruttori
    //
    public Immagine(Date dataCaricamento, String path, int idOggetto) {
        this.dataCaricamento = dataCaricamento;
        this.path = path;
        this.idOggetto = idOggetto;
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

    public int getIdOggetto() {
        return idOggetto;
    }

    public void setIdOggetto(int idOggetto) {
        this.idOggetto = idOggetto;
    }

    //toString
    //
    @Override
    public String toString() {
        return "Id: " + this.id + " Data Caricamento: " + this.dataCaricamento + " Path: " + this.path + " Id Oggetto: " + this.idOggetto;
    }
}
