package com.example.uninaswap.entity;
import java.util.ArrayList;

public class Oggetto {

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

    private int id;
    private String nome;
    private ArrayList<Categoria> categorie;
    private ArrayList<String> immagini;
    private Utente proprietario;
    private ArrayList<Categoria> Categorie = new ArrayList<Categoria>();
    private Annuncio annuncio;
    private OffertaScambio offertaScambio;

    Oggetto(String nome, Categoria categoria, ArrayList<String> immagini, Utente proprietario) {
        this.nome = nome;
        this.immagini = immagini;
        this.proprietario = proprietario;
        Categorie.add(categoria);
        this.proprietario.addOggetto(this);
        if (immagini.isEmpty()) {
            System.err.println("Attenzione: non e' stata aggiunta alcuna immagine");
        }
    }

    public void setOffertaScambio(OffertaScambio offertaScambio) {
        this.offertaScambio = offertaScambio;
    }

    public OffertaScambio getOffertaScambio() {
        return offertaScambio;
    }

    public void setAnnuncio(Annuncio annuncio) {
        this.annuncio = annuncio;
    }
    public Annuncio getAnnuncio() {
        return annuncio;
    }

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
        return  proprietario;
    }

    public void nuovoProprietario(Utente proprietario) {
        this.proprietario = proprietario;
    }

    public void modificaCategorie(ArrayList<Categoria> modificaCategoria) {
        this.categorie.clear();
        if (modificaCategoria != null) {
            for (Categoria categorie : modificaCategoria) {
                if (categorie != null) {
                    this.categorie.add(categorie);
                }
            }
        }
    }


    public ArrayList<Categoria> getCategorie() {
        return categorie;
    }

    public void aggiungiCategoria(Categoria categorie) {
        if (categorie != null) {
            this.categorie.add(categorie);
        }
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

    public void aggiungiImmagine(String immagini) {
        if (immagini != null) {
            this.immagini.add(immagini);
        }
    }





}
