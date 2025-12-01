package entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta{

    private AnnuncioRegalo annuncioRegalo;

    public OffertaRegalo(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioRegalo annuncioRegalo) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.annuncioRegalo = annuncioRegalo;
        this.annuncioRegalo.setOffertaRegali(this);
    }

    public AnnuncioRegalo getAnnuncioRegalo() {
        return annuncioRegalo;
    }

    public void setAnnuncioRegalo(AnnuncioRegalo annuncioRegalo) {
        this.annuncioRegalo = annuncioRegalo;
    }

}
