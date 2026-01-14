package com.example.uninaswap.entity;

import java.time.LocalTime;

public abstract class Offerta {

    //Enum
    // Meglio public per poter essere usata fuori dal package
    //
    public enum STATO_OFFERTA {
        RIFIUTATA,
        IN_ATTESA,
        ACCETTATA
    }

    protected String messaggio;
    protected STATO_OFFERTA stato;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;
    protected Oggetto oggetto;
    protected Annuncio annuncio;
    protected int id;
    protected Utente utente;

    // Costruttore
    public Offerta(Annuncio annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente) throws Exception {
        this.annuncio = annuncio;
        this.messaggio = messaggio;
        this.stato = stato;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.oggetto = oggetto;
        this.utente = utente;
        if (this.utente != null) {
            this.utente.setOfferta(this);
        }
        if (this.annuncio != null) {
            this.annuncio.ottieniOfferta(this);
        }
    }

    // --- Getter e Setter ---

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public STATO_OFFERTA getStato() {
        return stato;
    }

    public void setStato(STATO_OFFERTA stato) {
        this.stato = stato;
    }

    public LocalTime getOrarioInizio() {
        return orarioInizio;
    }

    public void setOrarioInizio(LocalTime orarioInizio) {
        this.orarioInizio = orarioInizio;
    }

    public LocalTime getOrarioFine() {
        return orarioFine;
    }

    public void setOrarioFine(LocalTime orarioFine) {
        this.orarioFine = orarioFine;
    }

    public Oggetto getOggetto() {
        return oggetto;
    }

    public void setOggetto(Oggetto oggetto) {
        this.oggetto = oggetto;
    }

    public void setOrari(LocalTime orarioInizio, LocalTime orarioFine) {
        if (orarioInizio == null || orarioFine == null) {
            throw new IllegalArgumentException("Entrambi gli orari devono essere specificati");
        }
        if (orarioInizio.isAfter(orarioFine)) {
            throw new IllegalArgumentException("L'orario d'inizio non può essere successivo all'orario di fine!");
        }
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
    }

    public void immettiOfferta(Annuncio annuncio) throws Exception {
        this.annuncio = annuncio;
        if (annuncio != null) {
            annuncio.ottieniOfferta(this);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    public Annuncio getAnnuncio() {
        return annuncio;
    }
    public void setAnnuncio(Annuncio annuncio) {
        this.annuncio = annuncio;
    }
}