package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.*;

public class OggettoDAO {
    public boolean salvaOggetto(Oggetto oggetto) {return true;}
    public boolean modificaOggetto(Oggetto oggetto) {return true;}
    public boolean eliminaOggetto(int id) {return true;}
    public boolean associaUtente(int idU, int idO){return true;}
    public boolean rimuoviDaUtente(int idU, int idO){return true;}
    public boolean associaAnnuncio(int idU, int idA){return true;}
    public boolean rimuoviDaAnnuncio(int idU, int idA){return true;}
    public Oggetto ottieniOggetto(int id){return null;}
    public ArrayList<Oggetto> ottieniTuttiOggetti(){return null;}
}
