package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class AnnuncioRegalo extends Annuncio{
    private ArrayList<OffertaRegalo> OffertaRegali = new ArrayList<OffertaRegalo>();

    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }
    public ArrayList<OffertaRegalo> getOffertaRegalo() {
        return OffertaRegali;
    }
    public void setOffertaRegali(OffertaRegalo offertaRegalo) {
        this.OffertaRegali.add(offertaRegalo);
    }
    @Override
    public void ottieniOfferta(Offerta offerta) throws Exception{
        if(offerta instanceof OffertaRegalo) {
            super.offerte.add((OffertaRegalo) offerta);
        }
        else{
            throw new Exception("Offerta di tipo sbagliato");
        }
    }
}
