package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.math.BigDecimal;

public class AnnuncioVendita extends Annuncio{

    //Attributi
    //
    private BigDecimal prezzoMedio;
    private BigDecimal prezzoMinimo;

    //Costruttori
    //
    public AnnuncioVendita() {
        super();
    }

    public AnnuncioVendita(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, BigDecimal prezzoMedio) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
        this.prezzoMedio = prezzoMedio;
    }

    //Getter e Setter
    public BigDecimal getPrezzoMedio() {
        return prezzoMedio;
    }
    public void setPrezzoMedio(BigDecimal prezzoMedio) {
        this.prezzoMedio = prezzoMedio;
    }

    public BigDecimal getPrezzoMinimo() {
        return prezzoMinimo;
    }
    public void setPrezzoMinimo(BigDecimal prezzoMinimo) {
        this.prezzoMinimo = prezzoMinimo;
    }

    //override di getTipoAnnuncio per specificare che è un AnnuncioVendita
    @Override
    public String getTipoAnnuncio(){
        return "Vendita";
    }

    //override per evitare che venga passata una specializzazione sbagliata di offerta
    @Override
    public void ottieniOfferta(Offerta offerta) throws Exception{
        if(offerta instanceof OffertaVendita) {
            super.offerte.add(offerta);
        }
        else{
            throw new Exception("Offerta di tipo sbagliato");
        }
    }

}
