package com.example.uninaswap.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

public class OffertaVendita extends Offerta{
    private BigDecimal prezzoOffertaVendita;;
    private AnnuncioVendita annuncioVendita;

    public OffertaVendita(Annuncio annuncio, String messaggio,
                          STATO_OFFERTA stato, LocalTime orarioInizio,
                          LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, AnnuncioVendita annuncioVendita)
            throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
        this.annuncioVendita = annuncioVendita;
        this.annuncioVendita.ottieniOfferta(this);
    }
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }
    public void setPrezzoOffertaVendita(BigDecimal prezzoOffertaVendita) {
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
    public AnnuncioVendita getannuncioVendita() {
        return annuncioVendita;
    }
    public void setAnnuncioVendita(AnnuncioVendita annuncioVendita) {
        this.annuncioVendita = annuncioVendita;
    }

//    public void immettiOfferta(Annuncio annuncio) throws Exception{
//        if(annuncio instanceof AnnuncioVendita) {
//            super.immettiOfferta(annuncio);
//            this.annuncioVendita = (AnnuncioVendita) annuncio;
//            annuncio.ottieniOfferta(this);
//        }
//        else{
//            throw new Exception("Non puoi fare un'offerta di scambio su quest'annuncio");
//        }
//    }

//    public void immettiOfferta(Annuncio annuncio) throws Exception{
//        if(annuncio instanceof AnnuncioVendita) {
//            super.immettiOfferta(annuncio);
//            this.annuncio = (AnnuncioVendita) annuncio;
//            annuncio.ottieniOfferta(this);
//        }
//        else{
//            throw new Exception("Non puoi fare un'offerta di scambio su quest'annuncio");
//        }
//    }

}
