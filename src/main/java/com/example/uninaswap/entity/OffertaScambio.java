package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class OffertaScambio extends Offerta {

    private ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();

    // Costruttore
    public OffertaScambio(AnnuncioScambio annuncio, String messaggio, STATO_OFFERTA stato,
                          LocalTime orarioInizio, LocalTime orarioFine,
                          Oggetto oggettoPrincipale, Utente utente) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggettoPrincipale, utente);
        if (oggettoPrincipale != null) {
            this.oggetti.add(oggettoPrincipale);
        }

    }

    public AnnuncioScambio getAnnuncioScambio() {
        if (this.annuncio instanceof AnnuncioScambio) {
            return (AnnuncioScambio) this.annuncio;
        }
        return null;
    }

    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }

    public void setOggetti(ArrayList<Oggetto> oggetti) {
        this.oggetti = oggetti;
    }

    public void aggiungiOggetto(Oggetto oggetto) {
        this.oggetti.add(oggetto);
    }

    public void rimuoviOggetto(Oggetto oggetto) {
        this.oggetti.remove(oggetto);
    }

    public void svuotaOggetti() {
        this.oggetti.clear();
    }

//    @Override
//    public void immettiOfferta(Annuncio nuovoAnnuncio) {
//        if (nuovoAnnuncio instanceof AnnuncioScambio) {
//            // Chiama la logica base (setta this.annuncio e fa il link bidirezionale)
//            super.immettiOfferta(nuovoAnnuncio);
//        } else {
//            throw new IllegalArgumentException("Non puoi fare un'offerta di scambio su questo tipo di annuncio");
//        }
//    }
}