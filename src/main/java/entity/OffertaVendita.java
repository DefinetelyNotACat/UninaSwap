package entity;

import java.math.BigDecimal;
import java.time.LocalTime;

public class OffertaVendita extends Offerta{
    private BigDecimal prezzoOffertaVendita;
    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, BigDecimal prezzoOffertaVendita) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }
    public void setPrezzoOffertaVendita(BigDecimal prezzoOffertaVendita) {
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
}
