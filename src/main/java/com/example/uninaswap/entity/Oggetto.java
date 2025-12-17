package com.example.uninaswap.entity;

import java.util.ArrayList;

public class Oggetto {

    // --- ENUM (Public per essere visti dal DAO) ---
    public enum DISPONIBILITA {
        DISPONIBILE, OCCUPATO, VENDUTO, REGALATO, SCAMBIATO
    }

    public enum CONDIZIONE {
        NUOVO, COME_NUOVO, OTTIME_CONDIZIONI, BUONE_CONDIZIONI, DISCRETE_CONDIZIONI, CATTIVE_CONDIZIONI
    }

    // --- ATTRIBUTI ---
    private int id;
    private String nome;
    private Utente proprietario;
    private Annuncio annuncio;

    // Relazione N:M -> Lista di categorie
    private ArrayList<Categoria> categorie = new ArrayList<>();

    // Default values
    private DISPONIBILITA disponibilita = DISPONIBILITA.DISPONIBILE;
    private CONDIZIONE condizione = CONDIZIONE.NUOVO;

    private ArrayList<String> immagini = new ArrayList<>();

    // --- COSTRUTTORI ---

    // Costruttore vuoto (essenziale per i DAO)
    public Oggetto() {}

    // Costruttore completo
    public Oggetto(String nome, ArrayList<Categoria> categorie, ArrayList<String> immaginiCaricate, Utente proprietario, CONDIZIONE condizione) {
        this.nome = nome;
        this.proprietario = proprietario;
        this.condizione = condizione;

        // Copia sicura delle categorie
        if (categorie != null) {
            this.categorie.addAll(categorie);
        }

        // Copia sicura delle immagini
        if (immaginiCaricate != null) {
            for (String img : immaginiCaricate) {
                if (img != null) this.immagini.add(img);
            }
        }

        // Associazione bidirezionale (opzionale, ma utile)
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

    // Gestione Categorie
    public ArrayList<Categoria> getCategorie() { return categorie; }
    public void setCategorie(ArrayList<Categoria> categorie) { this.categorie = categorie; }
    public void addCategoria(Categoria c) { if(c!=null) this.categorie.add(c); }

    // Gestione Immagini
    public ArrayList<String> getImmagini() { return immagini; }
    public void setImmagini(ArrayList<String> immagini) { this.immagini = immagini; }
    public void addImmagine(String img) { if(img!=null) this.immagini.add(img); }

    @Override
    public String toString() {
        return "Oggetto [id=" + id + ", nome=" + nome + ", categorie=" + categorie + "]";
    }
}