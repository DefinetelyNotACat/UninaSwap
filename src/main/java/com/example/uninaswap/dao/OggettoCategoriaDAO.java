package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoCategoriaDAO;

import java.sql.*;
import java.util.ArrayList;

public class OggettoCategoriaDAO implements GestoreOggettoCategoriaDAO {

    /**
     * Collega una categoria ad un oggetto nella tabella ponte.
     */
    public boolean associaCategoria(int idOggetto, String nomeCategoria) {
        String sql = "INSERT INTO OGGETTO_CATEGORIA (oggetto_id, categoria_nome) VALUES (?, ?)";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            stmt.setString(2, nomeCategoria);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Gestione errore (es. se la coppia esiste già, violazione Primary Key)
            System.err.println("Errore durante l'associazione Oggetto-Categoria: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rimuove il collegamento tra una specifica categoria e un oggetto.
     */
    public boolean rimuoviAssociazione(int idOggetto, String nomeCategoria) {
        String sql = "DELETE FROM OGGETTO_CATEGORIA WHERE oggetto_id = ? AND categoria_nome = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            stmt.setString(2, nomeCategoria);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove TUTTE le categorie associate a un oggetto.
     * Utile prima di salvare una modifica per fare "reset" delle categorie.
     */
    public boolean rimuoviTutteLeCategorieDiOggetto(int idOggetto, Connection connEsterna) throws SQLException {
        String sql = "DELETE FROM OGGETTO_CATEGORIA WHERE oggetto_id = ?";

        // Qui usiamo una logica flessibile: se ci passano una connessione (dentro una transazione) usiamo quella,
        // altrimenti ne apriamo una nuova.
        PreparedStatement stmt = null;
        boolean nuovaConnessione = (connEsterna == null);
        Connection conn = nuovaConnessione ? PostgreSQLConnection.getConnection() : connEsterna;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idOggetto);
            return stmt.executeUpdate() >= 0; // >= 0 perché potrebbe non averne nessuna, ma non è errore
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (nuovaConnessione && conn != null) conn.close();
        }
    }

    /**
     * Restituisce la lista di oggetti Categoria associati a un ID oggetto.
     */
    public ArrayList<Categoria> ottieniCategoriePerOggetto(int idOggetto) {
        ArrayList<Categoria> lista = new ArrayList<>();
        String sql = "SELECT categoria_nome FROM OGGETTO_CATEGORIA WHERE oggetto_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeCat = rs.getString("categoria_nome");
                    lista.add(new Categoria(nomeCat));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Metodo helper per salvare una lista intera in transazione (usato da OggettoDAO)
     */
    public void salvaListaCategorie(Connection conn, int idOggetto, ArrayList<Categoria> categorie) throws SQLException {
        String sql = "INSERT INTO OGGETTO_CATEGORIA (oggetto_id, categoria_nome) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Categoria cat : categorie) {
                stmt.setInt(1, idOggetto);
                stmt.setString(2, cat.getNome());
                stmt.addBatch(); // Ottimizzazione: prepara tutti gli inserimenti
            }
            stmt.executeBatch(); // Esegue tutti insieme
        }
    }
}
