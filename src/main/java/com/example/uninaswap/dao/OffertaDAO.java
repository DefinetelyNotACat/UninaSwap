package com.example.uninaswap.dao;

import com.example.uninaswap.entity.*;
import java.sql.*;
import java.util.ArrayList;

public class OffertaDAO {

    // DAO di supporto per caricare oggetti e utenti se necessario
    private OggettoDAO oggettoDAO = new OggettoDAO();
    private UtenteDAO utenteDAO = new UtenteDAO();
    private AnnuncioDAO annuncioDAO = new AnnuncioDAO();

    /**
     * SALVA OFFERTA (Gestisce Vendita, Scambio e Regalo)
     */
    public boolean salvaOfferta(Offerta offerta) {
        String sql = "INSERT INTO OFFERTA (utente_id, annuncio_id, tipo_offerta, messaggio, stato, orario_inizio, orario_fine, prezzo_offerta) " +
                "VALUES (?, ?, ?, ?, ?::stato_offerta, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // Transazione necessaria per OffertaScambio

            int idGenerato = -1;

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Parametri comuni
                ps.setInt(1, offerta.getUtente().getId()); // Assumiamo che l'oggetto Utente abbia l'ID settato
                ps.setInt(2, offerta.getAnnuncio().getId());

                // Determinazione Tipo
                if (offerta instanceof OffertaVendita) ps.setString(3, "VENDITA");
                else if (offerta instanceof OffertaScambio) ps.setString(3, "SCAMBIO");
                else ps.setString(3, "REGALO");

                ps.setString(4, offerta.getMessaggio());
                ps.setString(5, "IN_ATTESA"); // Default all'inserimento
                ps.setObject(6, offerta.getOrarioInizio());
                ps.setObject(7, offerta.getOrarioFine());

                // Prezzo (solo per Vendita)
                if (offerta instanceof OffertaVendita) {
                    ps.setDouble(8, ((OffertaVendita) offerta).getPrezzoOffertaVendita().doubleValue());
                } else {
                    ps.setNull(8, Types.DOUBLE);
                }

                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("Inserimento offerta fallito");

                // Recupero ID generato
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerato = rs.getInt(1);
                        offerta.setId(idGenerato);
                    } else {
                        throw new SQLException("ID offerta non generato");
                    }
                }
            }

            // GESTIONE SPECIFICA SCAMBIO: Aggiornamento Oggetti offerti
            if (offerta instanceof OffertaScambio) {
                OffertaScambio os = (OffertaScambio) offerta;
                if (os.getOggetti() != null && !os.getOggetti().isEmpty()) {
                    // Aggiorniamo la tabella OGGETTO settando l'offerta_id e lo stato a OCCUPATO
                    String sqlUpdateOggetti = "UPDATE OGGETTO SET offerta_id = ?, disponibilita = 'OCCUPATO'::disponibilita_oggetto WHERE id = ?";
                    try (PreparedStatement psObj = conn.prepareStatement(sqlUpdateOggetti)) {
                        for (Oggetto obj : os.getOggetti()) {
                            psObj.setInt(1, idGenerato);
                            psObj.setInt(2, obj.getId());
                            psObj.addBatch();
                        }
                        psObj.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * MODIFICA STATO (Accettazione/Rifiuto)
     */
    public boolean modificaStatoOfferta(int idOfferta, Offerta.STATO_OFFERTA nuovoStato) {
        String sql = "UPDATE OFFERTA SET stato = ?::stato_offerta WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoStato.toString());
            ps.setInt(2, idOfferta);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ELIMINA OFFERTA
     * Se è uno scambio, bisogna liberare gli oggetti prima di cancellare l'offerta.
     */
    public boolean eliminaOfferta(int idOfferta) {
        String sqlLiberaOggetti = "UPDATE OGGETTO SET offerta_id = NULL, disponibilita = 'DISPONIBILE'::disponibilita_oggetto WHERE offerta_id = ?";
        String sqlElimina = "DELETE FROM OFFERTA WHERE id = ?";

        Connection conn = null;
        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Libera oggetti (se esistono collegati a questa offerta)
            try (PreparedStatement ps = conn.prepareStatement(sqlLiberaOggetti)) {
                ps.setInt(1, idOfferta);
                ps.executeUpdate();
            }

            // 2. Elimina offerta
            try (PreparedStatement ps = conn.prepareStatement(sqlElimina)) {
                ps.setInt(1, idOfferta);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
        }
    }

    /**
     * OTTIENI OFFERTE PER UN ANNUNCIO
     */
    public ArrayList<Offerta> ottieniOffertePerAnnuncio(int idAnnuncio) {
        ArrayList<Offerta> lista = new ArrayList<>();
        String sql = "SELECT * FROM OFFERTA WHERE annuncio_id = ? ORDER BY data_creazione DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRowToOfferta(conn, rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * MAPPER: Converte una riga del DB in un oggetto Java corretto (Polimorfico)
     */
    private Offerta mapRowToOfferta(Connection conn, ResultSet rs) throws Exception {
        String tipo = rs.getString("tipo_offerta");
        int idOfferta = rs.getInt("id");
        int utenteId = rs.getInt("utente_id");
        int annuncioId = rs.getInt("annuncio_id");

        // Recuperiamo le entità correlate (o Proxy)
        Utente utente = utenteDAO.ottieniUtente(utenteId);
        Annuncio annuncio = annuncioDAO.OttieniAnnuncio(    annuncioId); // Attenzione: questo potrebbe essere pesante se fatto in loop

        // Parametri base
        String messaggio = rs.getString("messaggio");
        Offerta.STATO_OFFERTA stato = Offerta.STATO_OFFERTA.valueOf(rs.getString("stato"));
        Time tStart = rs.getTime("orario_inizio");
        Time tEnd = rs.getTime("orario_fine");

        Offerta offerta = null;

        if ("VENDITA".equalsIgnoreCase(tipo)) {
            java.math.BigDecimal prezzo = java.math.BigDecimal.valueOf(rs.getDouble("prezzo_offerta"));
            // Nota: AnnuncioVendita cast necessario nel costruttore se stretto, qui passo null per brevità o casto annuncio
            offerta = new OffertaVendita(annuncio, messaggio, stato,
                    tStart.toLocalTime(), tEnd.toLocalTime(), null, utente, prezzo, (AnnuncioVendita) annuncio);

        } else if ("SCAMBIO".equalsIgnoreCase(tipo)) {
            // Per lo scambio, dobbiamo recuperare gli oggetti offerti
            OffertaScambio os = new OffertaScambio((AnnuncioScambio) annuncio, messaggio, stato,
                    tStart.toLocalTime(), tEnd.toLocalTime(), null, utente);

            os.setOggetti(recuperaOggettiOfferta(conn, idOfferta));
            offerta = os;

        } else {
            // Regalo
            offerta = new OffertaRegalo(annuncio, messaggio, stato,
                    tStart.toLocalTime(), tEnd.toLocalTime(), null, utente, (AnnuncioRegalo)annuncio);
        }

        offerta.setId(idOfferta);
        return offerta;
    }

    private ArrayList<Oggetto> recuperaOggettiOfferta(Connection conn, int idOfferta) throws SQLException {
        ArrayList<Oggetto> oggetti = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE offerta_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOfferta);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    Oggetto o = new Oggetto();
                    o.setId(rs.getInt("id"));
                    o.setNome(rs.getString("nome"));
                    oggetti.add(o);
                }
            }
        }
        return oggetti;
    }
}