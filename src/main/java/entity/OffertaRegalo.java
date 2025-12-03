package entity;

import java.time.LocalTime;

<<<<<<< Updated upstream
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
=======
public class OffertaRegalo extends Offerta {
    public OffertaRegalo(AnnuncioRegalo annuncio, String messaggio, STATO_OFFERTA stato,
                         LocalTime orarioInizio, LocalTime orarioFine,
                         Oggetto oggetto, Utente utente) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        annuncio.aggiungiOffertaRegalo(this);
        annuncio.ottieniOfferta(this);
    }

    public AnnuncioRegalo getAnnuncioRegalo() {
        return (AnnuncioRegalo) this.annuncio;
    }
    public void setAnnuncioRegalo(AnnuncioRegalo annuncioRegalo) {
        this.annuncio = annuncioRegalo;
    }

//    @Override
//    public void immettiOfferta(Annuncio nuovoAnnuncio) throws Exception {
//        if (nuovoAnnuncio instanceof AnnuncioRegalo) {
//            super.immettiOfferta(nuovoAnnuncio);
//            AnnuncioRegalo regalo = (AnnuncioRegalo) nuovoAnnuncio;
//            regalo.setOffertaRegali(this);
//            regalo.ottieniOfferta(this);
//        } else {
//            throw new IllegalArgumentException("Un'OffertaRegalo può essere fatta solo su un AnnuncioRegalo");
//        }
//    }
}
>>>>>>> Stashed changes
