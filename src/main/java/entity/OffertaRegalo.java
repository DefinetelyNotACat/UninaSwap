package entity;

import java.time.LocalTime;

<<<<<<< Updated upstream
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
