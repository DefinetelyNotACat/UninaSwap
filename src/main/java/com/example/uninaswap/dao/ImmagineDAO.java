package com.example.uninaswap.dao;

import com.example.uninaswap.entity.Immagine;
import com.example.uninaswap.interfaces.GestoreImmagineDAO;

import java.sql.*;
import java.util.ArrayList;

public class ImmagineDAO implements GestoreImmagineDAO {

    public boolean inserisciImmagine(Immagine immagine, int idOggetto){
        // Delega al metodo che accetta la connessione, aprendone una nuova
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            return inserisciImmagine(conn, immagine, idOggetto);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inserisciImmagine(Connection conn, Immagine immagine, int idOggetto) throws SQLException {
        // Nota: nomi colonne allineati al tuo schema DB creato prima
        String sql = "INSERT INTO IMMAGINE (data_caricamento, path, oggetto_id) VALUES (CURRENT_TIMESTAMP, ?, ?)";

        // NON chiudiamo la connessione qui (try-with-resources solo su statement)
        try (PreparedStatement query = conn.prepareStatement(sql)) {
            // Nota: data_caricamento lo gestiamo con DEFAULT CURRENT_TIMESTAMP nel DB o qui
            // Se vuoi passarlo da java: query.setTimestamp(1, ...);

            query.setString(1, immagine.getPath());
            query.setInt(2, idOggetto);

            return query.executeUpdate() > 0;
        }
    }

    public boolean rimuoviImmagine(Immagine immagine){
        String sql = "DELETE FROM immagine WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Immagine> ottieniImmagini(int idOggetto) {
        ArrayList<Immagine> lista = new ArrayList<>();
        String sql = "SELECT * FROM IMMAGINE WHERE oggetto_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, idOggetto);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    Immagine img = new Immagine(); // Assumo costruttore vuoto o set
                    img.setId(rs.getInt("id"));
                    img.setPath(rs.getString("path"));
                    img.setDataCaricamento(rs.getTimestamp("data_caricamento")); // Usa Timestamp per data+ora
                    img.setIdOggetto(rs.getInt("oggetto_id"));
                    lista.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<Immagine> ottieniTutteImmagini(){
        return null;
    }
}
