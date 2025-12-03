package entity;

import java.time.LocalTime;
import java.util.ArrayList;

<<<<<<< Updated upstream
public class AnnuncioRegalo extends Annuncio{
    private ArrayList<OffertaRegalo> OffertaRegali = new ArrayList<>();

    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }

    public ArrayList<OffertaRegalo> getOffertaRegalo() {
        return OffertaRegali;
=======
public class AnnuncioRegalo extends Annuncio {
    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }
    public ArrayList<OffertaRegalo> getOfferteRegalo() {
        ArrayList<OffertaRegalo> listaTipizzata = new ArrayList<OffertaRegalo>();
        for (Offerta o : super.offerte) {
            if (o instanceof OffertaRegalo) {
                listaTipizzata.add((OffertaRegalo) o);
            }
        }
        return listaTipizzata;
>>>>>>> Stashed changes
    }
    public void aggiungiOffertaRegalo(OffertaRegalo offertaRegalo) {
        super.offerte.add(offertaRegalo);
    }
<<<<<<< Updated upstream
}
=======

    @Override
    public void ottieniOfferta(Offerta offerta) {
        // Usiamo IllegalArgumentException che è più corretta di Exception generica
        if (offerta instanceof OffertaRegalo) {
            super.offerte.add(offerta);
        } else {
            throw new IllegalArgumentException("Si possono aggiungere solo offerte di regalo a un AnnuncioRegalo");
        }
    }
}
>>>>>>> Stashed changes
