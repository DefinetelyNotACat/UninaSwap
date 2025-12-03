package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Recensione;

import java.util.List;

public interface GestoreRecensione {
    public Recensione OttieniRecensione(int id);
    public List<Recensione> OttieniTutteRecensione();
    public boolean SalvaRecensione(Recensione recensione);
    public boolean ModificaRecensione(Recensione recensione);
    public boolean EliminaRecensione(int id);
}
