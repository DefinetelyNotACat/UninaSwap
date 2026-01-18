package com.example.uninaswap.entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta {

    // Costruttore
    public OffertaRegalo(Annuncio annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioRegalo annuncioRegalo) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
    }

}