package entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta{

    private OffertaRegalo offertaRegalo;

    public OffertaRegalo(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, OffertaVendita offertaVendita) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.offertaRegalo = offertaRegalo;
    }
    public OffertaRegalo getOffertaRegalo() {
        return offertaRegalo;
    }
    public void setOffertaRegalo(OffertaRegalo offertaRegalo) {
        this.offertaRegalo = offertaRegalo;
    }

}
