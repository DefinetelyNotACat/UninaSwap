package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Immagine;
import com.example.uninaswap.interfaces.GestoreCondizioneDAO;
import com.example.uninaswap.interfaces.GestoreImmagineDAO;

import java.sql.*;
import java.util.ArrayList;

public class ImmagineDAO implements GestoreImmagineDAO {

    // Metodo per transazione: accetta Connection e lista di stringhe
    public void inserisciImmaginiBatch(Connection conn, int idOggetto, ArrayList<String> paths) throws SQLException {
        if (paths == null || paths.isEmpty()) return;

        String sql = "INSERT INTO IMMAGINE (path, oggetto_id, data_caricamento) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String path : paths) {
                stmt.setString(1, path);
                stmt.setInt(2, idOggetto);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public ArrayList<String> ottieniImmaginiStringhe(int idOggetto) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT path FROM IMMAGINE WHERE oggetto_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    lista.add(rs.getString("path"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void rimuoviImmaginiPerOggetto(Connection conn, int idOggetto) throws SQLException {
        // ATTENZIONE: Controlla il nome della tabella e della colonna
        String sql = "DELETE FROM IMMAGINE WHERE oggetto_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOggetto);
            stmt.executeUpdate();
        }
    }
}