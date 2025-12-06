package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Immagine;

import java.util.ArrayList;

public interface GestoreImmagineDAO {
    public boolean inserisciImmagine(Immagine immagine);
    public boolean rimuoviImmagine(Immagine immagine);
    public Immagine ottieniImmaigne(int id);
    public ArrayList<Immagine> ottieniTutteImmagini();
}
