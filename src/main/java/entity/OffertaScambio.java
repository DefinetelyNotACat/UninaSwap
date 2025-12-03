package entity;

import java.time.LocalTime;
import java.util.ArrayList;

<<<<<<< Updated upstream
public class OffertaScambio extends Offerta{
    private ArrayList<Oggetto> Oggetti = new ArrayList<Oggetto>();
    private AnnuncioScambio annuncioScambio;
    public OffertaScambio(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioScambio annuncioScambio) throws Exception {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.Oggetti.add(oggetto);
        this.annuncioScambio = annuncioScambio;
=======
public class OffertaScambio extends Offerta {

    private ArrayList<Oggetto> oggetti = new ArrayList<>();
    public OffertaScambio(AnnuncioScambio annuncio, String messaggio, STATO_OFFERTA stato,
                          LocalTime orarioInizio, LocalTime orarioFine,
                          Oggetto oggettoPrincipale, Utente utente) throws Exception {
        super(annuncio, messaggio, stato, orarioInizio, orarioFine, oggettoPrincipale, utente);
        if (oggettoPrincipale != null) {
            this.oggetti.add(oggettoPrincipale);
        }
        annuncio.ottieniOfferta(this);
    }
    public AnnuncioScambio getAnnuncioScambio() {
        return (AnnuncioScambio) this.annuncio;
>>>>>>> Stashed changes
    }
    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }
    public void setOggetti(ArrayList<Oggetto> oggetti) {
        this.oggetti = oggetti;
    }
    public void aggiungiOggetto(Oggetto oggetto) {
        this.oggetti.add(oggetto);
    }
    public void rimuoviOggetto(Oggetto oggetto) {
        this.oggetti.remove(oggetto);
    }
    public void svuotaOggetti() {
        this.oggetti.clear();
    }
//    @Override
//    public void immettiOfferta(Annuncio nuovoAnnuncio) throws Exception {
//        if (nuovoAnnuncio instanceof AnnuncioScambio) {
//            super.immettiOfferta(nuovoAnnuncio);
//            AnnuncioScambio scambio = (AnnuncioScambio) nuovoAnnuncio;
//            scambio.ottieniOfferta(this);
//        } else {
//            throw new IllegalArgumentException("Non puoi fare un'offerta di scambio su questo tipo di annuncio");
//        }
//    }
}