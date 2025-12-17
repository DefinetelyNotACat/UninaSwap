package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreCategoriaDAO;

public class CategoriaDAO implements GestoreCategoriaDAO {

    public boolean salvaCategoria(Categoria categoria){
        String sql = "INSERT INTO CATEGORIA (nome) VALUES (?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, categoria.getNome());
            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Categoria> OttieniCategorie(){
        ArrayList <Categoria> categorie = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIA";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                categorie.add(new Categoria(rs.getString("nome")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categorie;
    }

    public Categoria OttieniCategoria(String nome){
        return new Categoria(nome); // Semplificazione se esiste
    }

    // Metodi elimina e modifica rimangono uguali al tuo codice
    public boolean eliminaCategoria(String nome){
        String sql = "DELETE FROM CATEGORIA WHERE nome = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, nome);
            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificaCategoria(Categoria categoria){
        return true; // Stub
    }
}