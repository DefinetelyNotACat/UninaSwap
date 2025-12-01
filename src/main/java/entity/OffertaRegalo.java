package entity;

import java.time.LocalTime;

public class OffertaRegalo extends Offerta{

    private AnnuncioRegalo annuncioRegalo;

    public OffertaRegalo(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioRegalo annuncioRegalo) throws Exception {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.annuncioRegalo = annuncioRegalo;
        this.annuncioRegalo.setOffertaRegali(this);
        this.annuncioRegalo.ottieniOfferta(this);
    }

    public AnnuncioRegalo getAnnuncioRegalo() {
        return annuncioRegalo;
    }

    public void setAnnuncioRegalo(AnnuncioRegalo annuncioRegalo) {
        this.annuncioRegalo = annuncioRegalo;
    }

    public void immettiOfferta(Annuncio annuncio) throws Exception{
        if(annuncio instanceof AnnuncioRegalo) {
            super.immettiOfferta(annuncio);
            this.annuncioRegalo = (AnnuncioRegalo) annuncio;
            annuncio.ottieniOfferta(this);
        }
        else{
            throw new Exception("Non puoi fare un'offerta di scambio su quest'annuncio");
        }
    }

}
