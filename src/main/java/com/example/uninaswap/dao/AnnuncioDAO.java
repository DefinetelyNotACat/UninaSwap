package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreAnnuncioDAO;

public class AnnuncioDAO implements GestoreAnnuncioDAO {

    // Helper per le query sugli oggetti (non strettamente necessario istanziarlo se usiamo query dirette, ma utile)
    private OggettoDAO oggettiDAO = new OggettoDAO();

    /**
     * INSERIMENTO ANNUNCIO
     */
    public boolean inserisciAnnuncio(Annuncio annuncio, int utenteId) {
        String sql = "INSERT INTO ANNUNCIO (utente_id, sede_id, tipo_annuncio, descrizione, orario_inizio, orario_fine, prezzo, prezzo_minimo, nomi_items_scambio, stato) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::stato_annuncio)";

        Connection conn = null;

        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // --- INIZIO TRANSAZIONE ---

            // 1. Inserimento Annuncio
            int idAnnuncioGenerato = -1;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Set parametri comuni
                ps.setInt(1, utenteId);

                if(annuncio.getSede() != null) ps.setInt(2, annuncio.getSede().getId());
                else ps.setNull(2, Types.INTEGER);

                ps.setString(3, annuncio.getTipoAnnuncio());
                ps.setString(4, annuncio.getDescrizione());
                ps.setObject(5, annuncio.getOrarioInizio());
                ps.setObject(6, annuncio.getOrarioFine());

                // 2. Gestione Polimorfismo e fix conversione BigDecimal
                if (annuncio instanceof AnnuncioVendita) {
                    AnnuncioVendita av = (AnnuncioVendita) annuncio;
                    // FIX: Convertire BigDecimal in double per il DB
                    ps.setDouble(7, av.getPrezzoMedio() != null ? av.getPrezzoMedio().doubleValue() : 0.0);
                    ps.setDouble(8, av.getPrezzoMinimo() != null ? av.getPrezzoMinimo().doubleValue() : 0.0);
                    ps.setNull(9, Types.VARCHAR);
                } else if (annuncio instanceof AnnuncioScambio) {
                    AnnuncioScambio as = (AnnuncioScambio) annuncio;
                    ps.setNull(7, Types.DOUBLE);
                    ps.setNull(8, Types.DOUBLE);
                    ps.setString(9, as.getListaOggetti());
                } else {
                    // Annuncio Regalo
                    ps.setNull(7, Types.DOUBLE);
                    ps.setNull(8, Types.DOUBLE);
                    ps.setNull(9, Types.VARCHAR);
                }

                ps.setString(10, "DISPONIBILE");

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creazione annuncio fallita.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idAnnuncioGenerato = generatedKeys.getInt(1);
                        annuncio.setId(idAnnuncioGenerato);
                    } else {
                        throw new SQLException("Nessun ID generato.");
                    }
                }
            }

            // 3. Associazione Oggetti
            if (annuncio.getOggetti() != null && !annuncio.getOggetti().isEmpty()) {
                associaOggettiAdAnnuncio(conn, idAnnuncioGenerato, annuncio.getOggetti());
            }

            conn.commit(); // --- COMMIT ---
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private void associaOggettiAdAnnuncio(Connection conn, int annuncioId, ArrayList<Oggetto> oggetti) throws SQLException {
        String sqlUpdateOggetto = "UPDATE OGGETTO SET annuncio_id = ? WHERE id = ?";
        try (PreparedStatement psObj = conn.prepareStatement(sqlUpdateOggetto)) {
            for (Oggetto obj : oggetti) {
                psObj.setInt(1, annuncioId);
                psObj.setInt(2, obj.getId());
                psObj.addBatch();
            }
            psObj.executeBatch();
        }
    }

    /**
     * MAPPER: Da ResultSet a Oggetto Java (Vendita/Scambio/Regalo)
     */
    private Annuncio mapRowToAnnuncio(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_annuncio");
        Annuncio annuncio; // Variabile principale

        if ("Vendita".equalsIgnoreCase(tipo)) {
            AnnuncioVendita av = new AnnuncioVendita();
            // FIX: Da double DB a BigDecimal Java
            av.setPrezzoMedio(BigDecimal.valueOf(rs.getDouble("prezzo")));
            av.setPrezzoMinimo(BigDecimal.valueOf(rs.getDouble("prezzo_minimo")));
            annuncio = av; // Assegno alla variabile padre
        } else if ("Scambio".equalsIgnoreCase(tipo)) {
            AnnuncioScambio as = new AnnuncioScambio();
            as.setListaOggetti(rs.getString("nomi_items_scambio"));
            annuncio = as; // Assegno alla variabile padre
        } else {
            // Regalo o Default
            // FIX: Qui c'era l'errore. Bisogna assegnare ad 'annuncio', non creare una variabile locale inutile.
            annuncio = new AnnuncioRegalo();
        }

        // Set campi comuni
        annuncio.setId(rs.getInt("id"));
        // annuncio.setTitolo(...); // Decommenta se hai aggiunto il campo titolo in Annuncio
        annuncio.setDescrizione(rs.getString("descrizione"));
        // annuncio.setOrarioInizio(...); // Mappa gli orari se necessario

        Utente u = new Utente();
        u.setId(rs.getInt("utente_id"));
        annuncio.setUtenteId(u.getId());

        Sede s = new Sede();
        s.setId(rs.getInt("sede_id"));
        annuncio.setSede(s);

        return annuncio;
    }

    private ArrayList<Oggetto> recuperaOggettiPerAnnuncio(Connection conn, int idAnnuncio) throws SQLException {
        ArrayList<Oggetto> oggetti = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE annuncio_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Oggetto obj = new Oggetto();
                    obj.setId(rs.getInt("id"));
                    obj.setNome(rs.getString("nome"));

                    String condStr = rs.getString("condizione");
                    if(condStr != null) {
                        try {
                            obj.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr.replace(" ", "_").toUpperCase()));
                        } catch (Exception e) {}
                    }
                    oggetti.add(obj);
                }
            }
        }
        return oggetti;
    }

    /**
     * IMPLEMENTAZIONE METODI INTERFACCIA
     */

    // Metodo interno privato o pubblico (lowerCase)
    public Annuncio ottieniAnnuncio(int id) {
        String sql = "SELECT * FROM ANNUNCIO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Annuncio annuncio = mapRowToAnnuncio(rs);
                    // Popoliamo la lista oggetti
                    annuncio.setOggetti(recuperaOggettiPerAnnuncio(conn, id));
                    return annuncio;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Metodo dell'interfaccia (PascalCase) -> Chiama quello sopra
    @Override
    public Annuncio OttieniAnnuncio(int id) {
        return ottieniAnnuncio(id);
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunci() {
        ArrayList<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ANNUNCIO ORDER BY id DESC"; // Ordine cronologico inverso

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Annuncio annuncio = mapRowToAnnuncio(rs);
                // Nota: se hai 1000 annunci, fare 1000 query qui è lento.
                // Per ora va bene, ma in futuro si usa il "Lazy Loading".
                annuncio.setOggetti(recuperaOggettiPerAnnuncio(conn, annuncio.getId()));
                lista.add(annuncio);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean modificaAnnuncio(Annuncio annuncio) {
        // Da implementare update
        return true;
    }

    @Override
    public boolean eliminaAnnuncio(int id) {
        String sql = "DELETE FROM ANNUNCIO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            // Nota: se il DB ha ON DELETE SET NULL sugli oggetti, ok.
            // Altrimenti devi prima scollegare gli oggetti.
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}