package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;

public class ImmagineDAO {

    // Metodo standard per inserimento singolo (apre/chiude connessione)
    public boolean inserisciImmagine(String path, int idOggetto) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            inserisciImmaginiBatch(conn, idOggetto, new ArrayList<String>() {{ add(path); }});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    // Metodo di lettura
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
}