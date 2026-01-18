package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.interfaces.GestoreCondizioneDAO;

import java.sql.*;
import java.util.ArrayList;

public class CondizioneDAO implements GestoreCondizioneDAO {

    @Override
    public ArrayList<Oggetto.CONDIZIONE> ottieniTutteCondizioni() {
        ArrayList<Oggetto.CONDIZIONE> listaCondizioni = new ArrayList<>();

        String sql = "SELECT unnest(enum_range(NULL::condizione_oggetto))";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                String valoreDb = rs.getString("unnest");

                if (valoreDb != null) {
                    try {
                        String nomeEnumCompatibile = valoreDb.toUpperCase().replace(" ", "_");

                        Oggetto.CONDIZIONE condizione = Oggetto.CONDIZIONE.valueOf(nomeEnumCompatibile);
                        listaCondizioni.add(condizione);

                    } catch (IllegalArgumentException e) {
                        System.err.println("ERRORE CRITICO: Il valore DB '" + valoreDb + "' non esiste nell'Enum Java!");
                    }
                }
            }
            return listaCondizioni;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}