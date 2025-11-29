package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class AnnuncioRegalo extends Annuncio{
    private ArrayList<OffertaRegalo> OffertaRegali = new ArrayList<>();

    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }

    public ArrayList<OffertaRegalo> getOffertaRegalo() {
        return OffertaRegali;
    }
    public void setOffertaRegali(OffertaRegalo offertaRegalo) {
        this.OffertaRegali.add(offertaRegalo);
    }
}
