package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Immagine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface GestoreImmagineDAO {
    public boolean inserisciImmagine(String immagine, int idOggetto);
    public boolean inserisciImmagine(Connection conn, String immagine, int idOggetto) throws SQLException;
    public boolean rimuoviImmagine(Immagine immagine);
    public ArrayList<Immagine> ottieniImmagini(int idOggetto);
    public ArrayList<String> ottieniImmaginiStringhe(int idOggetto);
    public ArrayList<Immagine> ottieniTutteImmagini();
}
