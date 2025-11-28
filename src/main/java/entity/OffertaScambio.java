package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class OffertaScambio extends Offerta{
    private ArrayList<Oggetto> Oggetti = new ArrayList<Oggetto>();
    private AnnuncioScambio annuncioScambio;
    public OffertaScambio(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto, Utente utente, AnnuncioScambio annuncioScambio) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto, utente);
        this.Oggetti.add(oggetto);
        this.annuncioScambio = annuncioScambio;
    }
    public ArrayList<Oggetto> getOggetti() {
        return Oggetti;
    }
    public void setOggetti(ArrayList<Oggetto> oggetti) {
        this.Oggetti = oggetti;
    }
    public void aggiungiOggetto(Oggetto oggetto) {
        this.Oggetti.add(oggetto);
    }
    public void rimuoviOggetto(Oggetto oggetto) {
        this.Oggetti.remove(oggetto);
    }
    public void svuotaOggetti() {
        this.Oggetti.clear();
    }
    @Override
    public void immettiOfferta(Annuncio annuncio) throws Exception{
        if(annuncio instanceof AnnuncioScambio) {
            super.immettiOfferta(annuncio);
            this.annuncioScambio = (AnnuncioScambio) annuncio;
            annuncio.ottieniOfferta(this);
        }
        else{
            throw new Exception("Non puoi fare un'offerta di scambio su quest'annuncio");
        }
    }
}
