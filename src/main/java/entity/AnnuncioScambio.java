package entity;

import java.time.LocalTime;
import java.util.ArrayList;
public class AnnuncioScambio extends Annuncio{
    private ArrayList<OffertaScambio> OfferteScambio = new ArrayList<OffertaScambio>();
    private String listaOggetti;
    public AnnuncioScambio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto,
                           String listaOggetti) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
        this.listaOggetti = listaOggetti;
    }
    public String getListaOggetti() {
        return listaOggetti;
    }
    public void setListaOggetti(String listaOggetti) {
        this.listaOggetti = listaOggetti;
    }
}
