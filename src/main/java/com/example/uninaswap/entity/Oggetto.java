package com.example.uninaswap.entity;
import java.util.ArrayList;

public class Oggetto {

    //Enum
    //
    public enum DISPONIBILITA {
        DISPONIBILE,
        OCCUPATO,
        VENDUTO,
        REGALATO,
        SCAMBIATO
    }

    public enum CONDIZIONE {
        NUOVO,
        COME_NUOVO,
        OTTIME_CONDIZIONI,
        BUONE_CONDIZIONI,
        DISCRETE_CONDIZIONI,
        CATTIVE_CONDIZIONI
    }

    //Credo vada tolto, non dovrebbe servire qui lo stato annuncio
    public enum STATO_ANNUNCIO {
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
    private Categoria categoria;
    private DISPONIBILITA disponibilita = DISPONIBILITA.DISPONIBILE;
    private CONDIZIONE condizione = CONDIZIONE.NUOVO;
    private STATO_ANNUNCIO statoAnnuncio = STATO_ANNUNCIO.DISPONIBILE;
    private ArrayList<String> immagini = new ArrayList<>();

    //Costruttori
    //
    public Oggetto(String nome,Categoria categoria, ArrayList<String> immaginiCaricate, Utente proprietario, CONDIZIONE condizione) {
        this.nome = nome;
        if (immaginiCaricate != null) {
            for (String immagini : immaginiCaricate) {
                if (immagini != null) {
                    this.immagini.add(immagini);
                }
            }
        }
        this.condizione = condizione;
        this.disponibilita = DISPONIBILITA.DISPONIBILE;
        this.proprietario = proprietario;
        this.categoria = categoria;
        this.proprietario.addOggetto(this);
        if (immagini.isEmpty()) {
            System.err.println("Attenzione: non e' stata aggiunta alcuna immagine");
        }
    }

    //Adder, Remover e Clearer
    //
    public boolean addImmagine(String immagine) {
        if (immagine != null) {
            return this.immagini.add(immagine);
        }
        return false;
    }

    public boolean revomeImmagine(String immagine){
        return this.immagini.remove(immagine);
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
    public int getId() { return id;}

    public void setId(int id) {
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

    public Categoria getCategorie() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public DISPONIBILITA getDisponibilita() {
        return disponibilita;
    }
    public void setDisponibilita(DISPONIBILITA disponibilita) {
        this.disponibilita = disponibilita;
    }
    public CONDIZIONE getCondizione() {
        return condizione;
    }
    public void setCondizione(CONDIZIONE condizione) {
        this.condizione = condizione;
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

    public ArrayList<String> getImmagini() {
        return immagini;
    }

    @Override
    public String toString() {
        return "Id : " + this.id + ", Nome : " + this.nome + ", Proprietario : " + this.proprietario;
    }

}
