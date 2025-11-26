package interfaces;

import entity.Offerta;
import entity.OffertaVendita;

import java.util.ArrayList;

public interface GestoreOfferta {
    public boolean salvaOffertaVendita(OffertaVendita offertaVendita );
    public boolean modficaOfferta(OffertaVendita offertaVendita);
    public boolean eliminaOfferta(OffertaVendita offertaVendita);
    public Offerta ottieniOfferta(OffertaVendita offertaVendita);
    public ArrayList<Offerta> OttieniTutteOfferte();
}
