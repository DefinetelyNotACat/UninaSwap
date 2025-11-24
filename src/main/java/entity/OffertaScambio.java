package entity;

import java.time.LocalTime;

public class OffertaScambio extends Offerta{
    public OffertaScambio(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto);
    }
}
