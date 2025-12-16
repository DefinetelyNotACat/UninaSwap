package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.uninaswap.entity.Immagine;
import com.example.uninaswap.dao.ImmagineDAO;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {

    public boolean salvaOggetto(Oggetto oggetto) {return true;}

    public boolean modificaOggetto(Oggetto oggetto) {return true;}

    public boolean eliminaOggetto(int idOggetto) {return true;}

    public boolean associaUtente(int idUtente, int idOggetto){return true;}

    public boolean rimuoviDaUtente(int idUtente, int idOggetto){return true;}

    public boolean associaAnnuncio(int idUtente, int idAnnuncio){return true;}

    public boolean rimuoviDaAnnuncio(int idUtente, int idAannuncio){return true;}

    public Oggetto ottieniOggetto(int idOggetto){
        String queryObj = "SELECT * FROM OGGETTO WHERE id = ?";
        Oggetto oggetto = null;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryObj)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    oggetto = new Oggetto();
                    // ... mappa i campi standard (id, nome, etc.) ...
                    oggetto.setId(rs.getInt("id"));
                    oggetto.setNome(rs.getString("nome"));
                    // ...

                    // USIAMO IL TUO DAO PER LE IMMAGINI
                    // Qui possiamo aprire una nuova connessione interna al metodo del DAO,
                    // non siamo in transazione critica.

                    ArrayList<Immagine> immagini = ImmagineDAO.ottieni
                    oggetto.setImmagini(immagini);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oggetto;
    }

    public ArrayList<Oggetto> ottieniTuttiOggetti(){return null;}
}
