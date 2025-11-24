package entity;

import java.time.LocalTime;

public class OffertaVendita extends Offerta{
    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto);
    }
}
