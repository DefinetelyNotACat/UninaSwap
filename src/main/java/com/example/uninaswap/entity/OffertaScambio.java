package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class OffertaScambio extends Offerta {

    //Attributi
    //
    private ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();

    //Costruttore
    //
    public OffertaScambio(AnnuncioScambio annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggettoPrincipale, Utente utente) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggettoPrincipale, utente);
        if (oggettoPrincipale != null) {
            this.oggetti.add(oggettoPrincipale);
        }

    }

    //Adder, Remover e Clearer
    //
    public void addOggetto(Oggetto oggetto) {
        this.oggetti.add(oggetto);
    }

    public void removeOggetto(Oggetto oggetto) {
        this.oggetti.remove(oggetto);
    }

    public void clearOggetti() {
        this.oggetti.clear();
    }

    //Getter e Setter
    //
    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }
    public void setOggetti(ArrayList<Oggetto> oggetti) {
        this.oggetti = oggetti;
    }

}