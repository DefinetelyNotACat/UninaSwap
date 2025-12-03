package com.example.uninaswap.entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta {

    private AnnuncioRegalo annuncioRegalo;

    // Costruttore
    public OffertaRegalo(Annuncio annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioRegalo annuncioRegalo) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.annuncioRegalo = annuncioRegalo;

        // Collega questa offerta all'annuncio passato
        // Nota: Utilizziamo il metodo polimorfico ottieniOfferta che abbiamo definito in AnnuncioRegalo
        this.annuncioRegalo.ottieniOfferta(this);
    }

    public AnnuncioRegalo getAnnuncioRegalo() {
        return annuncioRegalo;
    }

    public void setAnnuncioRegalo(AnnuncioRegalo annuncioRegalo) {
        this.annuncioRegalo = annuncioRegalo;
    }

//    @Override
//    public void immettiOfferta(Annuncio annuncio) {
//        if (annuncio instanceof AnnuncioRegalo) {
//            // Chiama il metodo padre se necessario per logiche generiche
//            // super.immettiOfferta(annuncio);
//
//            // Imposta il riferimento specifico
//            this.annuncioRegalo = (AnnuncioRegalo) annuncio;
//
//            // Aggiunge questa offerta alla lista dell'annuncio
//            annuncio.ottieniOfferta(this);
//        } else {
//            throw new IllegalArgumentException("Non puoi fare un'offerta di scambio su un annuncio di regalo");
//        }
//    }
}