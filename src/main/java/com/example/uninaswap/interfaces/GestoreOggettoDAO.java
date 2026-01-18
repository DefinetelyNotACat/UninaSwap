package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;

import java.util.ArrayList;

public interface GestoreOggettoDAO {
    public boolean salvaOggetto(Oggetto oggetto, Utente utente);
    public boolean modificaOggetto(Oggetto oggetto);
    public boolean eliminaOggetto(int id);
    public Oggetto ottieniOggetto(int id, Utente utente);
    public ArrayList<Oggetto> ottieniTuttiOggetti(int idUtente);
    public ArrayList<Oggetto> ottieniTuttiOggettiDisponibili(int idUtente);
}
