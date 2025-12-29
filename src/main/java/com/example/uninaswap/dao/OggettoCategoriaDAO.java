package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoCategoriaDAO;

import java.sql.*;
import java.util.ArrayList;

public class OggettoCategoriaDAO implements GestoreOggettoCategoriaDAO {

    /**
     * Collega una singola categoria (Nuova connessione, no transazione).
     */
    public boolean associaCategoria(int idOggetto, String nomeCategoria) {
        String sql = "INSERT INTO OGGETTO_CATEGORIA (oggetto_id, categoria_nome) VALUES (?, ?)";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            stmt.setString(2, nomeCategoria);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Errore associazione Oggetto-Categoria: " + e.getMessage());
            return false;
        }
    }

    /**
     * Collega una LISTA di categorie usando una connessione ESISTENTE (Per Transazioni).
     * Sostituisce il vecchio metodo 'salvaListaCategorie'.
     */
    public void associaCategorie(Connection conn, int idOggetto, ArrayList<Categoria> categorie) throws SQLException {
        if (categorie == null || categorie.isEmpty()) return;

        String sql = "INSERT INTO OGGETTO_CATEGORIA (oggetto_id, categoria_nome) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Categoria cat : categorie) {
                stmt.setInt(1, idOggetto);
                stmt.setString(2, cat.getNome());
                stmt.addBatch(); // Aggiunge al batch
            }
            stmt.executeBatch(); // Esegue tutto insieme
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
     */
    public boolean rimuoviTutteLeCategorieDiOggetto(int idOggetto, Connection connEsterna) throws SQLException {
        String sql = "DELETE FROM OGGETTO_CATEGORIA WHERE oggetto_id = ?";

        PreparedStatement stmt = null;
        boolean chiudiConnessione = (connEsterna == null);
        Connection conn = chiudiConnessione ? PostgreSQLConnection.getConnection() : connEsterna;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idOggetto);
            // executeUpdate ritorna il numero di righe cancellate.
            // Se ritorna 0 non è un errore (l'oggetto non aveva categorie), quindi torniamo true comunque se non ci sono eccezioni.
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (chiudiConnessione && conn != null) conn.close();
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

    @Override
    public void salvaListaCategorie(Connection conn, int idOggetto, ArrayList<Categoria> categorie) throws SQLException {

    }

    public void eliminaCategoriePerOggetto(Connection conn, int idOggetto) throws SQLException {
        // Assumo che la tabella di collegamento si chiami OGGETTO_CATEGORIA
        // e abbia colonne 'oggetto_id' e 'categoria_nome' (o 'categoria_id').
        // Adatta i nomi se nel tuo DB sono diversi!
        String sql = "DELETE FROM OGGETTO_CATEGORIA WHERE oggetto_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOggetto);
            stmt.executeUpdate();
        }
    }

}