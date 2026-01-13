package com.example.uninaswap.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static com.example.uninaswap.Costanti.pathUtenti;

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
        // Map the Enum Constant -> "Human Readable Label"
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
                return CONDIZIONE.valueOf(text.toUpperCase());
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
        try {
            if (immaginiCaricate != null) {
                for (String img : immaginiCaricate) {
                    if (img != null) this.immagini.add(modificaImmagineProfilo(img));
                }
            }
        }catch (Exception e){
            System.err.println("Errore caricamento immagini: " + e.getMessage());
            e.printStackTrace();
        }

        if(this.proprietario != null) {
            this.proprietario.addOggetto(this);
        }
    }


    public String modificaImmagineProfilo(String pathImmagineCaricata) throws IOException {
        String pathImmagineOggetto = null;
        System.out.println("Sono nella modifica e l'id e': " + this.id);
        //Genera il path di destinazione dell'immagine: dati_utenti/oggetti/{id}/immagini
        //
        Path cartellaUtente = Paths.get(pathUtenti, "oggetti",  String.valueOf(this.id), "immagini");

        System.out.println("La cartella per le immagini e': " + Paths.get(cartellaUtente.toString()));

        //Se le cartelle non esistono le crea tutte in una sola volta
        //
        if (!Files.exists(cartellaUtente)) {
            System.out.println("La cartella per le immagini non esiste, la creo");

            Files.createDirectories(cartellaUtente);
        }

        //Fa diventare la stringa passata un oggetto di tipo Path
        //
        Path sorgenteImmagineCaricata = Paths.get(pathImmagineCaricata);

        //Faccio diventare la stringa contenente il path del file che l'utente sta caricando in un oggetto di tipo Path (Paths.get())
        //in maniera tale da ricavarci solo l'ultima parte che diventera' a sua volta un oggetto Path (.getFileName()) e poi lo passiamo come stringa attraverso il .toString()
        //
        String immagineCaricata = Paths.get(pathImmagineCaricata).getFileName().toString();

        //Ci ricaviamo l'estensione dal file appena caricato
        //
        int indiceEstensione = immagineCaricata.lastIndexOf('.');
        String estensioneFile = immagineCaricata.substring(indiceEstensione);

        //Diamo un nome al file che andremo a salvare
        //
        String nomeFileFinale = "immagine_1" + estensioneFile;

        //Definizione della del path che l'immagine avra' dopo il salvataggio attraverso cartellaUtente.resolve(nomeFileFile), resolve unisce il path della cartella con il nome del file
        //
        Path pathDestinazione = cartellaUtente.resolve(nomeFileFinale);


        System.out.println("Questo e' il path dell'immagine originale : " + sorgenteImmagineCaricata.toString());

        //Copia del file caricato nella cartella di destinazione con il nuovo nome, in caso il file esista gia' lo sovrascrive
        //
        Files.copy(sorgenteImmagineCaricata, pathDestinazione,  StandardCopyOption.REPLACE_EXISTING);

        //Ritorna come stringa il path relativo dell'immagine
        //
        return pathImmagineOggetto = Paths.get(String.valueOf(id), "immagini", nomeFileFinale).toString();

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