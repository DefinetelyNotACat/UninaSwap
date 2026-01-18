package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreCategoriaDAO;

public class CategoriaDAO implements GestoreCategoriaDAO {
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
}