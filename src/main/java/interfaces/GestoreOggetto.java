package interfaces;

import entity.Oggetto;

import java.util.ArrayList;

public interface GestoreOggetto {
    public boolean salvaOggetto(Oggetto oggetto);
    public boolean modificaOggetto(Oggetto oggetto);
    public boolean eliminaOggetto(int id);
    public boolean associaUtente(int idU, int idO);
    public boolean rimuoviDaUtente(int idU, int idO);
    public boolean associaAnnuncio(int idU, int idA);
    public boolean rimuoviDaAnnuncio(int idU, int idA);
    public Oggetto ottieniOggetto(int id);
    public ArrayList<Oggetto> ottieniTuttiOggetti();
}
