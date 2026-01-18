package com.example.uninaswap.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.io.File;

import static com.example.uninaswap.Costanti.pathUtenti;

public class Oggetto {

    //ENUM
    //
    public enum DISPONIBILITA {
        DISPONIBILE("Disponibile"), OCCUPATO("Occupato"), VENDUTO("Venduto"), REGALATO("Regalato"), SCAMBIATO("Scambiato");
        private final String etichetta;
        DISPONIBILITA(String etichetta) { this.etichetta = etichetta; }
        public String getEtichetta() { return etichetta; }
        @Override public String toString() { return etichetta; }
        public static DISPONIBILITA fromString(String text) {
            for (DISPONIBILITA b : DISPONIBILITA.values()) { if (b.etichetta.equalsIgnoreCase(text)) return b; }
            try { return DISPONIBILITA.valueOf(text.replace(" ", "_").toUpperCase()); } catch (Exception e) { return null; }
        }
    }

    public enum CONDIZIONE {
        NUOVO("Nuovo"), COME_NUOVO("Come Nuovo"), OTTIME_CONDIZIONI("Ottime Condizioni"), BUONE_CONDIZIONI("Buone Condizioni"), DISCRETE_CONDIZIONI("Discrete Condizioni"), CATTIVE_CONDIZIONI("Cattive Condizioni");
        private final String etichetta;
        CONDIZIONE(String etichetta) { this.etichetta = etichetta; }
        public String getEtichetta() { return etichetta; }
        @Override public String toString() { return etichetta; }
        public static CONDIZIONE fromString(String text) {
            for (CONDIZIONE c : CONDIZIONE.values()) { if (c.etichetta.equalsIgnoreCase(text)) return c; }
            try { return CONDIZIONE.valueOf(text.replace(" ", "_").toUpperCase()); } catch (Exception e) { return null; }
        }
    }

    //Attributi
    //
    private int id;
    private String nome;
    private Utente proprietario;
    private Annuncio annuncio;

    private DISPONIBILITA disponibilita = DISPONIBILITA.DISPONIBILE;
    private CONDIZIONE condizione = CONDIZIONE.NUOVO;

    private ArrayList<Categoria> categorie = new ArrayList<>();
    private ArrayList<String> immagini = new ArrayList<>();

    //Costruttori
    //
    public Oggetto() {}

    public Oggetto(String nome, ArrayList<Categoria> categorie, ArrayList<String> immaginiCaricate, Utente proprietario, CONDIZIONE condizione) {
        this.nome = nome;
        this.proprietario = proprietario;
        this.condizione = condizione;
        if (categorie != null) this.categorie.addAll(categorie);
        if (immaginiCaricate != null) this.immagini.addAll(immaginiCaricate);
        if(this.proprietario != null) this.proprietario.addOggetto(this);
    }

    //Metodi di logica
    //
    public String copiaImmagineInLocale(String pathSorgenteAssoluto) throws IOException {
        if (pathSorgenteAssoluto == null || pathSorgenteAssoluto.isEmpty()) return null;

        // 1. PUNTA SOLO A "oggetti", senza aggiungere String.valueOf(this.id)
        Path directoryOggetti = Paths.get("dati_utenti", "oggetti");

        if (!Files.exists(directoryOggetti)) {
            Files.createDirectories(directoryOggetti);
        }

        Path sorgente = Paths.get(pathSorgenteAssoluto);
        String nomeOriginale = sorgente.getFileName().toString();

        // Manteniamo il timestamp per evitare che due oggetti diversi con foto
        // chiamate "foto.jpg" si sovrascrivano a vicenda
        String nomeFileFinale = System.currentTimeMillis() + "_" + nomeOriginale;
        Path destinazioneFinale = directoryOggetti.resolve(nomeFileFinale);

        Files.copy(sorgente, destinazioneFinale, StandardCopyOption.REPLACE_EXISTING);

        // 2. RITORNA IL PATH PULITO: solo "oggetti/nomefile.jpg"
        return "oggetti/" + nomeFileFinale;
    }


    //Setter e Getter
    //
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public void setProprietario(Utente proprietario) { this.proprietario = proprietario; }

    public Annuncio getAnnuncio() { return annuncio; }
    public void setAnnuncio(Annuncio annuncio) { this.annuncio = annuncio; }

    public DISPONIBILITA getDisponibilita() { return disponibilita; }
    public void setDisponibilita(DISPONIBILITA disponibilita) { this.disponibilita = disponibilita; }

    public CONDIZIONE getCondizione() { return condizione; }
    public void setCondizione(CONDIZIONE condizione) { this.condizione = condizione; }

    public ArrayList<Categoria> getCategorie() { return categorie; }
    public void setCategorie(ArrayList<Categoria> categorie) { this.categorie = categorie; }

    public ArrayList<String> getImmagini() { return immagini; }
    public void setImmagini(ArrayList<String> immagini) { this.immagini = immagini; }

    //toString
    //
    @Override public String toString() {
        return "Oggetto [id=" + id + ", nome=" + nome + ", condizione=" + condizione + "]";
    }

}