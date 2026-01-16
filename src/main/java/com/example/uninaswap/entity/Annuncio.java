package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Annuncio {

    // --- ENUM STATO ---
    protected enum STATO_ANNUNCIO {
        DISPONIBILE,
        NON_DISPONIBILE
    }

    // --- CAMPI DATI ---
    protected int id;
    protected int utenteId;
    protected Utente utente; // Il venditore (Popolato tramite JOIN nel DAO)
    protected Sede sede;
    protected String descrizione;
    protected LocalTime orarioInizio;
    protected LocalTime orarioFine;
    protected STATO_ANNUNCIO stato;

    // Liste per gestire le relazioni
    protected ArrayList<Oggetto> oggetti = new ArrayList<>();
    protected ArrayList<Offerta> offerte = new ArrayList<>();

    // --- COSTRUTTORI ---
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

    // --- GETTER E SETTER (FIXATI) ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }

    // Questo è il metodo che ti dava errore: ora è completo
    public void setUtente(Utente venditore) {
        this.utente = venditore;
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

    // --- LOGICA DI BUSINESS ---

    public void setOrari(LocalTime orarioInizio, LocalTime orarioFine) throws Exception {
        if (orarioInizio == null || orarioFine == null) {
            throw new Exception("Entrambi gli orari devono essere specificati");
        }
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
                if (oggetto != null) this.oggetti.add(oggetto);
            }
        }
    }

    public void aggiungiOggetto(Oggetto oggetto) {
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

    @Override
    public String toString() {
        return "Annuncio{" +
                "id=" + id +
                ", descrizione='" + descrizione + '\'' +
                ", venditore=" + (utente != null ? utente.getUsername() : "N/A") +
                ", sede=" + (sede != null ? sede.getNomeSede() : "N/A") +
                ", stato=" + stato +
                ", num_oggetti=" + oggetti.size() +
                '}';
    }
}