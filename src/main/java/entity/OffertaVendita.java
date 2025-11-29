package entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
public class OffertaVendita extends Offerta{
    private BigDecimal prezzoOffertaVendita;
    private ArrayList<OffertaVendita> OfferteVendita = new ArrayList<OffertaVendita>();

    private OffertaVendita offertaVendita;

    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, OffertaVendita offertaVendita) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
        this.offertaVendita = offertaVendita;
    }
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }
    public void setPrezzoOffertaVendita(BigDecimal prezzoOffertaVendita) {
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
    public OffertaVendita getOffertaVendita() {
        return offertaVendita;
    }
    public void OffertaVendita(OffertaVendita offertaVendita) {
        this.offertaVendita = offertaVendita;
    }
}
