package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class Oggetto {

    private enum DISPONIBILITA {
        DISPONIBILE,
        OCCUPATO,
        VENDUTO,
        REGALATO,
        SCAMBIATO
    }

    private enum CONDIZIONE {
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
    private Annuncio annuncio;
    private OffertaScambio offertascambio;

    Oggetto(String nome, ArrayList<Categoria> categorie, ArrayList<String> immagini, Utente proprietario) {
        this.nome = nome;
        this.categorie = categorie;
        this.immagini = immagini;
        this.proprietario = proprietario;
        if (immagini.isEmpty()) {
            System.err.println("Attenzione: non e' stata aggiunta alcuna immagine");
        }
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

    public void setCategorie(ArrayList<Categoria> modificaCategoria) {
        this.categorie.clear();
        if (modificaCategoria != null) {
            for (Categoria categorie : modificaCategoria) {
                if (categorie != null) {
                    this.categorie.add(categorie);
                }
            }
        }
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

    public void setAnnuncio(Annuncio annuncio) {
        this.annuncio = annuncio;
    }

    public Annuncio getAnnuncio() {
        return annuncio;
    }

    public void setOffertascambio(OffertaScambio offertascambio) {
        this.offertascambio = offertascambio;
    }

    public OffertaScambio getOffertascambio() {
        return offertascambio;
    }

}
