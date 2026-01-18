package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoCategoriaDAO;

import java.sql.*;
import java.util.ArrayList;

public class OggettoCategoriaDAO implements GestoreOggettoCategoriaDAO {

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