package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.example.uninaswap.entity.Annuncio;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.interfaces.GestoreAnnuncioDAO;

public class AnnuncioDAO implements GestoreAnnuncioDAO {
    public boolean inserisciAnnuncio(Annuncio annuncio, int idSede, ArrayList<Oggetto> nuoviOggetti) {
        String sql = "INSERT INTO ANNUNCI (idSede, descrizione, orarioInizio, orarioFine, stato, ) VALUES ( ?, ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setInt(1, idSede);
            query.setString(2, annuncio.getDescrizione());
            query.setObject(3, annuncio.getOrarioInizio());
            query.setObject(4, annuncio.getOrarioFine());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean modificaAnnuncio(Annuncio annuncio){return true;}
    public boolean eliminaAnnuncio(int id){return true;}
    public Annuncio OttieniAnnuncio(int id){return null;}
    public ArrayList<Annuncio> OttieniAnnunci(){return null;}
}
