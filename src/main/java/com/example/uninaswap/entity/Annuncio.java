package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Annuncio {

    // ENUM
    //
    public enum STATO_ANNUNCIO {
        DISPONIBILE,
        NON_DISPONIBILE
    }

    //Attributi
    //
    protected int id;
    protected int utenteId;
    protected Utente utente; // Il venditore (Popolato tramite JOIN nel DAO)
    protected Sede sede;
    protected String descrizione;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;

    protected STATO_ANNUNCIO stato;

    protected ArrayList<Oggetto> oggetti = new ArrayList<>();
    protected ArrayList<Offerta> offerte = new ArrayList<>();

    //Costruttori
    //
    public Annuncio() {
        this.stato = STATO_ANNUNCIO.DISPONIBILE;
    }

    public Annuncio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        this.sede = sede;
        if (this.sede != null) {
            this.sede.aggiungiAnnuncio(this);
        }
        this.descrizione = descrizione;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.stato = STATO_ANNUNCIO.DISPONIBILE;

        if (oggetto != null) {
            this.oggetti.add(oggetto);
            try {
                oggetto.setAnnuncio(this);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    //Getter e Setter
    //
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    public Utente getUtente() {
        return utente;
    }

    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public LocalTime getOrarioInizio() { return orarioInizio; }

    public LocalTime getOrarioFine() { return orarioFine; }

    public void setStato(STATO_ANNUNCIO stato) { this.stato = stato; }
    public STATO_ANNUNCIO getStato() { return stato; }

    public ArrayList<Oggetto> getOggetti() { return oggetti; }

    public void setOggetti(ArrayList<Oggetto> nuoviOggetti) {
        this.oggetti.clear();
        if (nuoviOggetti != null) {
            for (Oggetto oggetto : nuoviOggetti) {
                if (oggetto != null) this.oggetti.add(oggetto);
            }
        }
    }

    public void addOggetto(Oggetto oggetto) {
        if (oggetto != null) this.oggetti.add(oggetto);
    }

    protected void ottieniOfferta(Offerta offerta) throws Exception {
        if (offerta != null) {
            this.offerte.add(offerta);
        } else {
            throw new Exception("L'Offerta non esiste");
        }
    }

    // Metodo astratto implementato dalle sottoclassi (Vendita, Scambio, Regalo)
    public abstract String getTipoAnnuncio();

    //toString
    @Override
    public String toString() {
        return "Annuncio{" + "id=" + id + ", descrizione='" + descrizione + '\'' +  ", venditore=" + (utente != null ? utente.getUsername() : "N/A") +  ", sede=" + (sede != null ? sede.getNomeSede() : "N/A") +  ", stato=" + stato +  ", num_oggetti=" + oggetti.size() + '}';
    }
}