package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreCondizioneDAO;

import java.sql.*;
import java.util.ArrayList;

public class CondizioneDAO implements GestoreCondizioneDAO {

    @Override
    public ArrayList<String> ottieniTutteCondizioni() {
        ArrayList<String> categorie = new ArrayList<>();
        String sql = "SELECT unnest(enum_range(NULL::condizione_oggetto))";
        try(Connection connessione = PostgreSQLConnection.getConnection();
            PreparedStatement query = connessione.prepareStatement(sql)){
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                String condizione = rs.getString("unnest");
                categorie.add(condizione);
            }
            return categorie;

        }   catch (SQLException e){
            e.printStackTrace();

        }
        return null;
    }
}
