package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.example.uninaswap.entity.Sede;
import com.example.uninaswap.interfaces.GestoreSedeDAO;

public class SedeDAO implements GestoreSedeDAO {

    public ArrayList<Sede> OttieniSedi(){
        ArrayList<Sede> OttieniSedi = new ArrayList<>();
        String sql = "SELECT * FROM SEDE";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                Sede sede = new Sede(
                        rs.getString("nome_sede"),
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
