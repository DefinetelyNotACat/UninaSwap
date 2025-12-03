package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Annuncio;

import java.util.ArrayList;

public interface GestoreAnnuncio {
    public boolean salvaAnnuncio(Annuncio annuncio);
    public boolean modificaAnnuncio(Annuncio annuncio);
    public boolean eliminaAnnuncio(int id);
    public Annuncio OttieniAnnuncio(int id);
    public ArrayList<Annuncio> OttieniAnnunci();
}
