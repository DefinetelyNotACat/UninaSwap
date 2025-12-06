package com.example.uninaswap.dao;

import java.util.ArrayList;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {
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
