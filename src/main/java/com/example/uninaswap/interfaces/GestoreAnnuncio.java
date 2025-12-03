package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Annuncio;

import java.util.ArrayList;

public interface GestoreAnnuncio {
    public boolean salvaAnnuncio(Annuncio annuncio);
    public boolean modificaAnnuncio(Annuncio annuncio);
    public boolean salvaAnnuncio(int id);
    public boolean OttieniAnnuncio(int id);
    public ArrayList<Annuncio> OttieniAnnunci();
}
