package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Annuncio {
    //enum per evitare che piu' utenti possano fare offerte allo stesso annuncio
    //uso di protected per poter dare l'accesso alle sottoclassi
    protected enum STATO_ANNUNCIO {
        DISPONIBILE,
        NON_DISPONIBILE
    }
    protected int id;
    protected int utenteId;
    protected Sede sede;
    protected String descrizione;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;
    protected STATO_ANNUNCIO stato;
    //liste per gestire la relazione tra oggetti e annunci
    protected ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();
    protected ArrayList<Offerta> offerte = new ArrayList<Offerta>();

    public Annuncio() {}

    public Annuncio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        this.sede = sede;
        //gestione relazione tra annuncio e sede: una volta creato l'annuncio, viene aggiunto a sede
        this.sede.aggiungiAnnuncio(this);
        this.descrizione = descrizione;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.stato = STATO_ANNUNCIO.DISPONIBILE;
        //gestione relazione tra annuncio e oggetto: una volta creato l'annuncio viene aggiunto fa un check per vedere
        //se l'oggetto inserito esiste, se si' l'annuncio viene aggiunto all'oggetto altrimenti da' un messaggio di errore
        if (oggetto != null) {
            try {
                this.oggetti.add(oggetto);
                oggetto.setAnnuncio(this);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
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
        //check per controllare che l'orario di inizio non superi quello di fine
        if (orarioInizio.isAfter(orarioFine)) {
            throw new Exception("L'orario d'inizio non può essere successivo a quello di fine!");
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

    //metodo necessario per la relazione tra annuncio e offerta: check che controlla che l'offerta esista prima di aggiungerla
    protected void ottieniOfferta(Offerta offerta) throws Exception{
        if (offerta != null) {
            this.offerte.add(offerta);
        }
        else{
            throw new Exception("L'Offerta non esiste");
        }
    }

    //metodo per distinguere tra le sottoclassi dell'annuncio
    public abstract String getTipoAnnuncio();

    @Override
    public String toString() {
        return "Annuncio{" +
                "descrizione='" + descrizione + '\'' +
                ", sede=" + (sede != null ? sede.getNomeSede() : "sede non impostata") +
                ", orario=" + orarioInizio + "-" + orarioFine +
                ", stato=" + stato +
                ", num oggetti=" + oggetti.size() +
                ", oggetti = " + oggetti +
                '}';
    }
}