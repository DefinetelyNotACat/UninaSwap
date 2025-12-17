package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Immagine;
import com.example.uninaswap.dao.ImmagineDAO;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {

    private ImmagineDAO immagineDAO = new ImmagineDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    public boolean salvaOggetto(Oggetto oggetto, Utente utente) {
        ArrayList<String> immaginiDaInserire = oggetto.getImmagini();
        for (String immagine : immaginiDaInserire) {
            immagineDAO.inserisciImmagine(immagine, oggetto.getId());
        }
        String sql = "INSERT INTO OGGETTO (utente_id, categoria_nome, nome, condizione, disponibilita) VALUES (?,?,?,?,?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setInt(1, utente.getId());
            query.setString(2, oggetto.getCategorie().getNome());
            query.setString(3, oggetto.getNome());
            query.setString(4, oggetto.getCondizione().toString());
            query.setString(5, oggetto.getDisponibilita().toString());
            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificaOggetto(Oggetto oggetto) {return true;}

    public boolean eliminaOggetto(int idOggetto) {return true;}

    public boolean associaUtente(int idUtente, int idOggetto){return true;}

    public boolean rimuoviDaUtente(int idUtente, int idOggetto){return true;}

    public boolean associaAnnuncio(int idUtente, int idAnnuncio){return true;}

    public boolean rimuoviDaAnnuncio(int idUtente, int idAannuncio){return true;}

    public Oggetto ottieniOggetto(int idOggetto, Utente utente){
        String queryObj = "SELECT * FROM OGGETTO WHERE id = ?";
        Oggetto oggetto = null;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryObj)) {

            stmt.setInt(1, idOggetto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Categoria categoria  = categoriaDAO.OttieniCategoria(rs.getString("categoria"));
                    ArrayList<String> immaginiOggetto = immagineDAO.ottieniImmaginiStringhe(idOggetto);
                    oggetto = new Oggetto(rs.getString("nome"), categoria, immaginiOggetto, utente, Oggetto.CONDIZIONE.valueOf(rs.getString("condizione")));
                    oggetto.setId(rs.getInt("id"));
                    oggetto.setNome(rs.getString("nome"));
                    oggetto.setDisponibilita(Oggetto.DISPONIBILITA.valueOf(rs.getString("disponibilita")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oggetto;
    }

    public ArrayList<Oggetto> ottieniTuttiOggetti(){return null;}
}
