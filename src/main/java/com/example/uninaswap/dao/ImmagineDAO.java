package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Immagine;
import com.example.uninaswap.interfaces.GestoreImmagineDAO;

import java.sql.*;
import java.util.ArrayList;

public class ImmagineDAO implements GestoreImmagineDAO {

    public boolean inserisciImmagine(String immagine, int idOggetto){
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            return inserisciImmagine(conn, immagine, idOggetto);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inserisciImmagine(Connection conn, String immagine, int idOggetto) throws SQLException {
        String sql = "INSERT INTO IMMAGINE (data_caricamento, path, oggetto_id) VALUES (CURRENT_TIMESTAMP, ?, ?)";
        try (PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, immagine);
            query.setInt(2, idOggetto);
            int numModifiche = query.executeUpdate();
            return numModifiche > 0;
        }
    }

    public boolean rimuoviImmagine(Immagine immagine){
        String sql = "DELETE FROM immagine WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, immagine.getId());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Immagine> ottieniImmagini(int idOggetto) {
        ArrayList<Immagine> lista = new ArrayList<>();
        String sql = "SELECT * FROM IMMAGINE WHERE oggetto_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, idOggetto);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    Immagine img = new Immagine((rs.getTimestamp("data_caricamento")), (rs.getString("path")), (rs.getInt("id"))); // Assumo costruttore vuoto o set
                    img.setId(rs.getInt("id"));
                    lista.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<String> ottieniImmaginiStringhe(int idOggetto) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT * FROM IMMAGINE WHERE oggetto_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, idOggetto);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String img = rs.getString("path");
                    lista.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;

    }

    public ArrayList<Immagine> ottieniTutteImmagini(){
        ArrayList<Immagine> lista = new ArrayList<>();
        String sql = "SELECT * FROM IMMAGINE";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    Immagine img = new Immagine((rs.getTimestamp("data_caricamento")), (rs.getString("path")), (rs.getInt("id"))); // Assumo costruttore vuoto o set
                    img.setId(rs.getInt("id"));
                    lista.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
