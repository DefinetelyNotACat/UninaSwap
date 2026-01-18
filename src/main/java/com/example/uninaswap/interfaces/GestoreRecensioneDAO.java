package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Recensione;

import java.util.List;

public interface GestoreRecensioneDAO {
    public boolean SalvaRecensione(Recensione recensione);
    public boolean ModificaRecensione(Recensione recensione);
}
