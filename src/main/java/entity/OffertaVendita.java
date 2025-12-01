package entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
public class OffertaVendita extends Offerta{
    private BigDecimal prezzoOffertaVendita;;
    private AnnuncioVendita annuncioVendita;

    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, AnnuncioVendita annuncioVendita) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
        this.annuncioVendita = annuncioVendita;
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
}
