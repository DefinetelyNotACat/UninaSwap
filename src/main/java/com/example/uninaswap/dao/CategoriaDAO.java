package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreCategoria;

public class CategoriaDAO implements GestoreCategoria {
    public boolean salvaCategoria(Categoria categoria){
        String sql = "INSERT INTO CATEGORIA (nome) VALUES (?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, categoria.getNome());
            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificaCategoria(Categoria categoria){
            String sql = "UPDATE CATEGORIA SET nome = ? WHERE nome = ?";
            try (Connection connessione = PostgreSQLConnection.getConnection();
                 PreparedStatement query = connessione.prepareStatement(sql)) {
                query.setString(1, categoria.getNome());
                int numModifiche = query.executeUpdate();
                return numModifiche > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
    }
    public boolean eliminaCategoria(String nome){
        String sql = "DELETE FROM CATEGORIA WHERE nome = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, nome);

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public Categoria  OttieniCategoria(String nome){
        Categoria categoria = null;
        String sql = "SELECT * FROM CATEGORIA WHERE nome = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);) {
            query.setString(1, nome);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(rs.getString("nome"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoria;
    }
    public ArrayList<Categoria> OttieniCategorie(){
        ArrayList<Categoria> categorie = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIA";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(rs.getString("nome"));
                categorie.add(categoria);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categorie;

    }
}

