package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Annuncio;
import com.example.uninaswap.entity.Oggetto;

import java.util.ArrayList;

public interface GestoreAnnuncioDAO {
    public boolean modificaAnnuncio(Annuncio annuncio);
    public boolean eliminaAnnuncio(int id);
    public Annuncio OttieniAnnuncio(int id);
    public ArrayList<Annuncio> OttieniAnnunci();
    public boolean inserisciAnnuncio(Annuncio annuncio, int utenteId);
}
