package entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta{

    public OffertaRegalo(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
    }
}
