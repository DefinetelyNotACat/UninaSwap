package entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
public class OffertaVendita extends Offerta{
    private BigDecimal prezzoOffertaVendita;
    private ArrayList<OffertaVendita> OfferteVendita = new ArrayList<OffertaVendita>();

    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, BigDecimal prezzoOffertaVendita, Utente utente) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }
    public void setPrezzoOffertaVendita(BigDecimal prezzoOffertaVendita) {
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
}
