package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;

import java.util.ArrayList;

public interface GestoreOggettoDAO {
    public boolean salvaOggetto(Oggetto oggetto, Utente utente);
    public boolean modificaOggetto(Oggetto oggetto);
    public boolean eliminaOggetto(int id);
    public boolean associaUtente(int idU, int idO);
    public boolean rimuoviDaUtente(int idU, int idO);
    public boolean associaAnnuncio(int idU, int idA);
    public boolean rimuoviDaAnnuncio(int idU, int idA);
    public Oggetto ottieniOggetto(int id, Utente utente);
    public ArrayList<Oggetto> ottieniTuttiOggetti();
}
