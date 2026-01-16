package com.example.uninaswap.dao;

import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreOffertaDAO;

import java.sql.*;
import java.util.ArrayList;

public class OffertaDAO implements GestoreOffertaDAO {

    // DAO di supporto per caricare oggetti e utenti se necessario
    private OggettoDAO oggettoDAO = new OggettoDAO();
    private UtenteDAO utenteDAO = new UtenteDAO();
    private AnnuncioDAO annuncioDAO = new AnnuncioDAO();


    public ArrayList<Offerta> ottieniOfferteInviate(int utenteId) {
        ArrayList<Offerta> lista = new ArrayList<>();
        String sql = "SELECT * FROM OFFERTA WHERE utente_id = ? ORDER BY data_creazione DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, utenteId);
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

    public ArrayList<Offerta> ottieniOfferteRicevute(int utenteId) {
        ArrayList<Offerta> lista = new ArrayList<>();
        // Selezioniamo tutte le offerte collegate ad annunci posseduti dall'utenteId
        String sql = "SELECT o.* FROM OFFERTA o " +
                "JOIN ANNUNCIO a ON o.annuncio_id = a.id " +
                "WHERE a.utente_id = ? " +
                "ORDER BY o.data_creazione DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, utenteId);
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
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * MODIFICA STATO (Accettazione/Rifiuto)
     */
    public boolean modificaStatoOfferta(int idOfferta, Offerta.STATO_OFFERTA nuovoStato) {
        String sqlUpdateStato = "UPDATE OFFERTA SET stato = ?::stato_offerta WHERE id = ?";

        // SQL per recuperare info vitali sull'offerta (tipo e annuncio collegato)
        String sqlInfo = "SELECT tipo_offerta, annuncio_id FROM OFFERTA WHERE id = ?";

        // SQL per aggiornare l'oggetto dell'annuncio (Chi riceve l'offerta)
        String sqlUpdateOggettoAnnuncio = "UPDATE OGGETTO SET disponibilita = ?::disponibilita_oggetto WHERE annuncio_id = ?";

        // SQL per aggiornare gli oggetti proposti nello scambio (Chi fa l'offerta)
        String sqlUpdateOggettiScambio = "UPDATE OGGETTO SET disponibilita = 'SCAMBIATO'::disponibilita_oggetto WHERE offerta_id = ?";

        Connection conn = null;
        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // TRANSAZIONE FONDAMENTALE

            // 1. Recupero informazioni sull'offerta
            String tipoOfferta = "";
            int idAnnuncio = -1;

            try (PreparedStatement psInfo = conn.prepareStatement(sqlInfo)) {
                psInfo.setInt(1, idOfferta);
                try (ResultSet rs = psInfo.executeQuery()) {
                    if (rs.next()) {
                        tipoOfferta = rs.getString("tipo_offerta");
                        idAnnuncio = rs.getInt("annuncio_id");
                    } else {
                        throw new SQLException("Offerta non trovata");
                    }
                }
            }

            // 2. Aggiorno lo stato dell'offerta
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStato)) {
                ps.setString(1, nuovoStato.toString());
                ps.setInt(2, idOfferta);
                ps.executeUpdate();
            }

            // 3. SE L'OFFERTA È ACCETTATA -> Aggiorno gli oggetti
            if (nuovoStato == Offerta.STATO_OFFERTA.ACCETTATA) {

                String nuovoStatoOggetto = "VENDUTO"; // Default per vendita
                if ("SCAMBIO".equalsIgnoreCase(tipoOfferta)) {
                    nuovoStatoOggetto = "SCAMBIATO";
                } else if ("REGALO".equalsIgnoreCase(tipoOfferta)) {
                    nuovoStatoOggetto = "REGALATO";
                }

                // A. Aggiorno l'oggetto contenuto nell'annuncio (Il mio oggetto)
                try (PreparedStatement psObjAnnuncio = conn.prepareStatement(sqlUpdateOggettoAnnuncio)) {
                    psObjAnnuncio.setString(1, nuovoStatoOggetto);
                    psObjAnnuncio.setInt(2, idAnnuncio);
                    psObjAnnuncio.executeUpdate();
                }

                // B. Se è uno SCAMBIO, aggiorno anche gli oggetti che l'altro utente mi ha dato
                if ("SCAMBIO".equalsIgnoreCase(tipoOfferta)) {
                    try (PreparedStatement psObjScambio = conn.prepareStatement(sqlUpdateOggettiScambio)) {
                        psObjScambio.setInt(1, idOfferta);
                        psObjScambio.executeUpdate();
                    }
                }

                // C. Opzionale: Se accetto un'offerta, dovrei rifiutare tutte le altre offerte In Attesa per lo stesso annuncio?
                // Per ora lo lasciamo semplice, ma in futuro potresti voler aggiungere questa logica.
            }

            // 4. SE L'OFFERTA È RIFIUTATA E TIPO SCAMBIO -> Libero gli oggetti dell'offerente
            if (nuovoStato == Offerta.STATO_OFFERTA.RIFIUTATA && "SCAMBIO".equalsIgnoreCase(tipoOfferta)) {
                String sqlLibera = "UPDATE OGGETTO SET disponibilita = 'DISPONIBILE'::disponibilita_oggetto, offerta_id = NULL WHERE offerta_id = ?";
                try (PreparedStatement psLibera = conn.prepareStatement(sqlLibera)) {
                    psLibera.setInt(1, idOfferta);
                    psLibera.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            return false;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
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
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            return false;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
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
        Annuncio annuncio = annuncioDAO.OttieniAnnuncio(annuncioId); // Attenzione: questo potrebbe essere pesante se fatto in loop

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
                    tStart.toLocalTime(), tEnd.toLocalTime(), null, utente, (AnnuncioRegalo) annuncio);
        }

        offerta.setId(idOfferta);
        return offerta;
    }

    // Sostituisci il metodo recuperaOggettiOfferta nel tuo OffertaDAO.java
    private ArrayList<Oggetto> recuperaOggettiOfferta(Connection conn, int idOfferta) throws SQLException {
        // Usiamo una mappa per evitare duplicati se un oggetto ha più immagini (prendiamo la prima)
        java.util.LinkedHashMap<Integer, Oggetto> mappaOggetti = new java.util.LinkedHashMap<>();

        String sql = "SELECT o.*, i.path as img_path " +
                "FROM OGGETTO o " +
                "LEFT JOIN IMMAGINE i ON o.id = i.oggetto_id " +
                "WHERE o.offerta_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOfferta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idObj = rs.getInt("id");
                    Oggetto o = mappaOggetti.get(idObj);

                    if (o == null) {
                        o = new Oggetto();
                        o.setId(idObj);
                        o.setNome(rs.getString("nome"));
                        String condStr = rs.getString("condizione");
                        if (condStr != null) {
                            o.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr.replace(" ", "_").toUpperCase()));
                        }
                        mappaOggetti.put(idObj, o);
                    }

                    // Aggiungiamo il path dell'immagine se esiste e non è già presente
                    String path = rs.getString("img_path");
                    if (path != null && !o.getImmagini().contains(path)) {
                        o.getImmagini().add(path);
                    }
                }
            }
        }
        return new ArrayList<>(mappaOggetti.values());
    }
}