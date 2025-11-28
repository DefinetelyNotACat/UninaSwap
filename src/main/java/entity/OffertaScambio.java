package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class OffertaScambio extends Offerta{
    private ArrayList<Oggetto> Oggetti = new ArrayList<Oggetto>();

    public OffertaScambio(String messaggio, STATO_OFFERTA stato, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(messaggio, stato, orarioInizio, orarioFine, oggetto);
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
}
