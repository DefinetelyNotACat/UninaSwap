package com.example.uninaswap.entity;

import java.util.ArrayList;

public class Oggetto {

    // --- ENUM ---

    public enum DISPONIBILITA {
        DISPONIBILE("Disponibile"),
        OCCUPATO("Occupato"),
        VENDUTO("Venduto"),
        REGALATO("Regalato"),
        SCAMBIATO("Scambiato");

        private final String etichetta;

        DISPONIBILITA(String etichetta) {
            this.etichetta = etichetta;
        }

        public String getEtichetta() {
            return etichetta;
        }

        @Override
        public String toString() {
            return etichetta;
        }

        public static DISPONIBILITA fromString(String text) {
            for (DISPONIBILITA b : DISPONIBILITA.values()) {
                if (b.etichetta.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            try {
                return DISPONIBILITA.valueOf(text.replace(" ", "_").toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }

    public enum CONDIZIONE {
        NUOVO("Nuovo"),
        COME_NUOVO("Come Nuovo"),
        OTTIME_CONDIZIONI("Ottime Condizioni"),
        BUONE_CONDIZIONI("Buone Condizioni"),
        DISCRETE_CONDIZIONI("Discrete Condizioni"),
        CATTIVE_CONDIZIONI("Cattive Condizioni");

        private final String etichetta;

        CONDIZIONE(String etichetta) {
            this.etichetta = etichetta;
        }

        public String getEtichetta() {
            return etichetta;
        }

        @Override
        public String toString() {
            return etichetta;
        }

        public static CONDIZIONE fromString(String text) {
            for (CONDIZIONE c : CONDIZIONE.values()) {
                if (c.etichetta.equalsIgnoreCase(text)) {
                    return c;
                }
            }
            try {
                return CONDIZIONE.valueOf(text.replace(" ", "_").toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }

    // --- ATTRIBUTI ---
    private int id;
    private String nome;
    private Utente proprietario;
    private Annuncio annuncio;

    private ArrayList<Categoria> categorie = new ArrayList<>();

    private DISPONIBILITA disponibilita = DISPONIBILITA.DISPONIBILE;
    private CONDIZIONE condizione = CONDIZIONE.NUOVO;

    private ArrayList<String> immagini = new ArrayList<>();

    // --- COSTRUTTORI ---

    public Oggetto() {}

    public Oggetto(String nome, ArrayList<Categoria> categorie, ArrayList<String> immaginiCaricate, Utente proprietario, CONDIZIONE condizione) {
        this.nome = nome;
        this.proprietario = proprietario;
        this.condizione = condizione;

        if (categorie != null) {
            this.categorie.addAll(categorie);
        }

        if (immaginiCaricate != null) {
            for (String img : immaginiCaricate) {
                if (img != null) this.immagini.add(img);
            }
        }

        if(this.proprietario != null) {
            this.proprietario.addOggetto(this);
        }
    }

    // --- GETTER E SETTER ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Utente getProprietario() { return proprietario; }
    public void setProprietario(Utente proprietario) { this.proprietario = proprietario; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }

    public CONDIZIONE getCondizione() { return condizione; }
    public void setCondizione(CONDIZIONE condizione) { this.condizione = condizione; }

    public DISPONIBILITA getDisponibilita() { return disponibilita; }
    public void setDisponibilita(DISPONIBILITA disponibilita) { this.disponibilita = disponibilita; }

    public ArrayList<Categoria> getCategorie() { return categorie; }
    public void setCategorie(ArrayList<Categoria> categorie) { this.categorie = categorie; }
    public void addCategoria(Categoria c) { if(c!=null) this.categorie.add(c); }

    public ArrayList<String> getImmagini() { return immagini; }
    public void setImmagini(ArrayList<String> immagini) { this.immagini = immagini; }
    public void addImmagine(String img) { if(img!=null) this.immagini.add(img); }

    @Override
    public String toString() {
        return "Oggetto [id=" + id + ", nome=" + nome + ", condizione=" + condizione+ "]";
    }
}