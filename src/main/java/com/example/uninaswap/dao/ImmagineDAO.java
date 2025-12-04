package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Immagine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ImmagineDAO {

    public boolean inserisciImmagine(Immagine immagine, int idOggetto){
        String sql = "INSERT INTO immagine (dataCaricamenteo, path, idOggetto) VALUES ( ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setDate(1, (Date) immagine.getDataCaricamento());
            query.setString(2, immagine.getPath());
            query.setInt(3, idOggetto);

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rimuoviImmagine(int id){
        String sql = "DELETE FROM immagine WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Immagine ottieniImmaigne(int id){
        Immagine immagine = null;
        String sql = "SELECT * FROM immagine WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);) {
            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    //immagine = new Immagine(rs.getDate("data_inserimento"), rs.getString("path"), rs.getInt("idOggetto"));
                    immagine.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return immagine;
    }

    public ArrayList<Immagine> ottieniTutteImmagini(){
        return null;
    }
}
