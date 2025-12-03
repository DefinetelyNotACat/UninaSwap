package entity;

import java.math.BigDecimal;
import java.time.LocalTime;

public class OffertaVendita extends Offerta{
<<<<<<< Updated upstream
    private BigDecimal prezzoOffertaVendita;
    private ArrayList<OffertaVendita> OfferteVendita = new ArrayList<OffertaVendita>();

    private OffertaVendita offertaVendita;

    public OffertaVendita(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, OffertaVendita offertaVendita) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
        this.offertaVendita = offertaVendita;
=======
    private BigDecimal prezzoOffertaVendita;;

    public OffertaVendita(AnnuncioVendita annuncio, String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, BigDecimal prezzoOffertaVendita, AnnuncioVendita annuncioVendita) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.prezzoOffertaVendita = prezzoOffertaVendita;
        this.annuncio = annuncioVendita;
        this.annuncio.ottieniOfferta(this);
>>>>>>> Stashed changes
    }
    public BigDecimal getPrezzoOffertaVendita() {
        return prezzoOffertaVendita;
    }
    public void setPrezzoOffertaVendita(BigDecimal prezzoOffertaVendita) {
        this.prezzoOffertaVendita = prezzoOffertaVendita;
    }
<<<<<<< Updated upstream
    public OffertaVendita getOffertaVendita() {
        return offertaVendita;
    }
    public void OffertaVendita(OffertaVendita offertaVendita) {
        this.offertaVendita = offertaVendita;
    }
=======
    public AnnuncioVendita getannuncioVendita() {
        return (AnnuncioVendita) this.annuncio;
    }

//    public void immettiOfferta(Annuncio annuncio) throws Exception{
//        if(annuncio instanceof AnnuncioVendita) {
//            super.immettiOfferta(annuncio);
//            this.annuncio = (AnnuncioVendita) annuncio;
//            annuncio.ottieniOfferta(this);
//        }
//        else{
//            throw new Exception("Non puoi fare un'offerta di scambio su quest'annuncio");
//        }
//    }
>>>>>>> Stashed changes
}
