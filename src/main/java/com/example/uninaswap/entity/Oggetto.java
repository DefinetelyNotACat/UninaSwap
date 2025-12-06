package com.example.uninaswap.entity;
import java.util.ArrayList;

public class Oggetto {

    //Enum
    //
    protected enum DISPONIBILITA {
        DISPONIBILE,
        OCCUPATO,
        VENDUTO,
        REGALATO,
        SCAMBIATO
    }
    protected enum CONDIZIONE {
        NUOVO,
        COME_NUOVO,
        OTTIME_CONDIZIONI,
        BUONE_CONDIZIONI,
        DISCRETE_CONDIZIONI,
        CATTIVE_COMDIZIONI
    }
    protected enum STATO_ANNUNCIO {
        DISPONIBILE,
        NONDISPONIBILE
    }

    //Attributi
    //
    private int id;
    private String nome;
    private Utente proprietario;
    private Annuncio annuncio;
    private OffertaScambio offertaScambio;
    private DISPONIBILITA disponibilita = DISPONIBILITA.DISPONIBILE;
    private ArrayList<Categoria> categorie;
    private ArrayList<String> immagini;
    private ArrayList<Categoria> Categorie = new ArrayList<Categoria>();

    //Costruttori
    //
    Oggetto(String nome, ArrayList<Categoria> categoria, ArrayList<String> immagini, Utente proprietario) {
        this.nome = nome;
        this.immagini = immagini;
        this.proprietario = proprietario;
        this.categorie = categoria;
        this.proprietario.addOggetto(this);
        if (immagini.isEmpty()) {
            System.err.println("Attenzione: non e' stata aggiunta alcuna immagine");
        }
    }

    //Metodi di logica
    //
    public void modificaCategorie(ArrayList<Categoria> modificaCategorie) {
        this.categorie.clear();
        if (modificaCategorie != null) {
            for (Categoria categorie : modificaCategorie) {
                if (categorie != null) {
                    this.categorie.add(categorie);
                }
            }
        }
    }

    //Adder, Remover e Clearer
    //
    public boolean addImmagine(String immagini) {
        if (immagini != null) {
            return this.immagini.add(immagini);
        }
        return false;
    }

    public boolean revomeImmagine(String immagini){
        return this.immagini.remove(immagini);
    }

    public boolean addCategoria(Categoria categorie) {
        if (categorie != null) {
            return this.categorie.add(categorie);
        }
        return false;
    }

    //Verificare nel caso di spostare questa funzione in annuncio//
    public void removeAnnuncio(Annuncio annuncio) throws Exception{
        ArrayList <Oggetto> oggetti = annuncio.getOggetti();
        for (Oggetto oggetto : oggetti) {
            oggetto.disponibilita = DISPONIBILITA.DISPONIBILE;
        }
        oggetti.clear();
        this.annuncio = null;
    }

    //Getter e Setter
    //
    public int getid() { return id;}

    public void setid(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String Nome) {
        this.nome = nome;
    }

    public Utente getProprietario() {
        return proprietario;
    }

    public void setProprietario(Utente proprietario) {
        this.proprietario = proprietario;
    }

    public void setAnnuncio(Annuncio annuncio) throws Exception{
        if (this.disponibilita == DISPONIBILITA.DISPONIBILE) {
            this.annuncio = annuncio;
            this.disponibilita = DISPONIBILITA.OCCUPATO;
        }
        else{
            throw new Exception("Oggetto occupato o inesistente");
        }

    }

    public Annuncio getAnnuncio() {
        return annuncio;
    }

    public void setOffertaScambio(OffertaScambio offertaScambio) {
        this.offertaScambio = offertaScambio;
    }

    public OffertaScambio getOffertaScambio() {
        return offertaScambio;
    }

    public ArrayList<Categoria> getCategorie() {
        return categorie;
    }

    public void setImmagini(ArrayList<String> nuoveImmagini) {
        this.immagini.clear();
        if (nuoveImmagini != null) {
            for (String immagini : nuoveImmagini) {
                if (immagini != null) {
                    this.immagini.add(immagini);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Id : " + this.id + ", Nome : " + this.nome + ", Proprietario : " + this.proprietario;
    }

}
