package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Annuncio {
    protected enum STATO_ANNUNCIO {
        DISPONIBILE,
        NONDISPONIBILE
    }
    protected Sede sede;
    protected String descrizione;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;
    protected STATO_ANNUNCIO stato;
    protected ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();
    protected ArrayList<Offerta> offerte = new ArrayList<Offerta>();

    public Annuncio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        this.sede = sede;
        this.sede.aggiungiAnnuncio(this);
        this.descrizione = descrizione;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.stato = STATO_ANNUNCIO.DISPONIBILE;
        if (oggetto != null) {
            this.oggetti.add(oggetto);
            oggetto.setAnnuncio(this);
        } else {
            System.err.println("Attenzione: creato annuncio senza oggetto iniziale.");
        }
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

    public STATO_ANNUNCIO getStato() {
        return stato;
    }

    public void setStato(STATO_ANNUNCIO stato) {
        this.stato = stato;
    }

    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }

    public void setOrari(LocalTime orarioInizio, LocalTime orarioFine) throws Exception {
        if (orarioInizio == null || orarioFine == null) {
            throw new Exception("Entrambi gli orari devono essere specificati");
        }
        if (orarioInizio.isAfter(orarioFine)) {
            throw new Exception("L'orario d'inizio non può essere successivo all'orario di fine!");
        }
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
    }
    public void setOggetti(ArrayList<Oggetto> nuoviOggetti) {
        this.oggetti.clear();
        if (nuoviOggetti != null) {
            for (Oggetto oggetto : nuoviOggetti) {
                if (oggetto != null) {
                    this.oggetti.add(oggetto);
                }
            }
        }
    }
    public void aggiungiOggetto(Oggetto oggetto) {
        if (oggetto != null) {
            this.oggetti.add(oggetto);
        }
    }
    protected void ottieniOfferta(Offerta offerta) throws Exception{
        if (offerta != null) {
            this.offerte.add(offerta);
        }
        else{
            throw new Exception("Offerta non esistente");
        }
    }
}