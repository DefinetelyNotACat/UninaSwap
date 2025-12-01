package entity;

import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.ArrayList;

public class AnnuncioVendita extends Annuncio{
    private BigDecimal prezzoMedio;
    private BigDecimal prezzoMinimo;
    private ArrayList<OffertaVendita> OfferteVendita = new ArrayList<OffertaVendita>();


    public AnnuncioVendita(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, BigDecimal prezzoMedio) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
        this.prezzoMedio = prezzoMedio;
    }
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
    @Override
    public void ottieniOfferta(Offerta offerta) throws Exception{
        if(offerta instanceof OffertaVendita) {
            super.offerte.add((OffertaVendita) offerta);
        }
        else{
            throw new Exception("Offerta di tipo sbagliato");
        }
    }
}
