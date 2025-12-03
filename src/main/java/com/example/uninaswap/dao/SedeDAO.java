package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.entity.Sede;
import com.example.uninaswap.interfaces.GestoreOggetto;
import com.example.uninaswap.interfaces.GestoreSede;

public class SedeDAO implements GestoreSede {
    public boolean salvaSede(Sede sede){
        String sql = "INSERT INTO SEDE (nomeSede, indirizzo) VALUES (?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, sede.getNomeSede());
            query.setString(2, sede.getIndirizzo());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean modificaSede(Sede sede){
        String sql = "UPDATE SEDE SET nomeSede = ?, indirizzo = ? WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, sede.getNomeSede());
            query.setString(2, sede.getIndirizzo());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminaSede(int id){
        String sql = "DELETE FROM SEDE WHERE id = ?";
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
    public Sede OttieniSede(int id){
        Sede sede = null;
        String sql = "SELECT * FROM SEDE WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    sede = new Sede(
                            rs.getString("nomeSede"),
                            rs.getString("indirizzo")
                    );
                   sede.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sede;

    }
    public ArrayList<Sede> OttieniSedi(){
        ArrayList<Sede> OttieniSedi = new ArrayList<>();
        String sql = "SELECT * FROM SEDE";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                Sede sede = new Sede(
                        rs.getString("nomeSede"),
                        rs.getString("indirizzo")
                );
                sede.setId(rs.getInt("id"));
                OttieniSedi.add(sede);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OttieniSedi;
    }
}
