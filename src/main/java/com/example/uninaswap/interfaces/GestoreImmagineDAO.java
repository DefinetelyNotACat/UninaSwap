package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Immagine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface GestoreImmagineDAO {
    public ArrayList<String> ottieniImmaginiStringhe(int idOggetto);
    public void rimuoviImmaginiPerOggetto(Connection conn, int idOggetto) throws SQLException;
    public void inserisciImmaginiBatch(Connection conn, int idOggetto, ArrayList<String> paths) throws SQLException;
}
