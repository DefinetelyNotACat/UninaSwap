package com.example.uninaswap.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

public class OffertaVendita extends Offerta{

    //Attributi
    //
    private BigDecimal prezzoOffertaVendita;

    //Costruttori
    //
    public OffertaVendita(Annuncio annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, AnnuncioVendita annuncioVendita) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;

    }

    //Getter e Setter
    //
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }

    }
