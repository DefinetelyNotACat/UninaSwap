package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreAnnuncioDAO;
// Assicurati di importare l'enum
import com.example.uninaswap.entity.Oggetto.DISPONIBILITA;

public class AnnuncioDAO implements GestoreAnnuncioDAO {

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

                // 2. Gestione Polimorfismo
                if (annuncio instanceof AnnuncioVendita) {
                    AnnuncioVendita av = (AnnuncioVendita) annuncio;
                    ps.setDouble(7, av.getPrezzoMedio() != null ? av.getPrezzoMedio().doubleValue() : 0.0);
                    ps.setDouble(8, av.getPrezzoMinimo() != null ? av.getPrezzoMinimo().doubleValue() : 0.0);
                    ps.setNull(9, Types.VARCHAR);
                } else if (annuncio instanceof AnnuncioScambio) {
                    AnnuncioScambio as = (AnnuncioScambio) annuncio;
                    ps.setNull(7, Types.DOUBLE);
                    ps.setNull(8, Types.DOUBLE);
                    ps.setString(9, as.getListaOggetti());
                } else {
                    ps.setNull(7, Types.DOUBLE);
                    ps.setNull(8, Types.DOUBLE);
                    ps.setNull(9, Types.VARCHAR);
                }

                ps.setString(10, "DISPONIBILE");

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) throw new SQLException("Creazione annuncio fallita.");

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idAnnuncioGenerato = generatedKeys.getInt(1);
                        annuncio.setId(idAnnuncioGenerato);
                    } else {
                        throw new SQLException("Nessun ID generato.");
                    }
                }
            }

            // 3. Associazione Oggetti e CAMBIO STATO -> OCCUPATO
            if (annuncio.getOggetti() != null && !annuncio.getOggetti().isEmpty()) {
                associaOggettiAdAnnuncio(conn, idAnnuncioGenerato, annuncio.getOggetti());

                // Aggiorniamo anche gli oggetti in memoria Java per coerenza immediata nell'UI
                for(Oggetto o : annuncio.getOggetti()) {
                    o.setDisponibilita(DISPONIBILITA.OCCUPATO);
                }
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

    /**
     * MODIFICATO: Oltre ad associare l'ID, imposta la disponibilità a OCCUPATO.
     */
    private void associaOggettiAdAnnuncio(Connection conn, int annuncioId, ArrayList<Oggetto> oggetti) throws SQLException {
        // Aggiunto: disponibilita = 'OCCUPATO'::disponibilita_oggetto
        String sqlUpdateOggetto = "UPDATE OGGETTO SET annuncio_id = ?, disponibilita = 'OCCUPATO'::disponibilita_oggetto WHERE id = ?";

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
     * MODIFICATO: Gestione transazionale per liberare gli oggetti prima di eliminare l'annuncio.
     */
    @Override
    public boolean eliminaAnnuncio(int id) {
        // Query A: Libera gli oggetti (toglie annuncio_id e mette DISPONIBILE)
        String sqlLiberaOggetti = "UPDATE OGGETTO SET annuncio_id = NULL, disponibilita = 'DISPONIBILE'::disponibilita_oggetto WHERE annuncio_id = ?";
        // Query B: Elimina l'annuncio
        String sqlEliminaAnnuncio = "DELETE FROM ANNUNCIO WHERE id = ?";

        Connection conn = null;
        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // Inizio Transazione

            // 1. Libero gli oggetti
            try (PreparedStatement psLibera = conn.prepareStatement(sqlLiberaOggetti)) {
                psLibera.setInt(1, id);
                psLibera.executeUpdate();
            }

            // 2. Elimino l'annuncio
            try (PreparedStatement psElimina = conn.prepareStatement(sqlEliminaAnnuncio)) {
                psElimina.setInt(1, id);
                int rows = psElimina.executeUpdate();

                if (rows > 0) {
                    conn.commit(); // Conferma tutto
                    return true;
                } else {
                    conn.rollback(); // Se non trova l'annuncio, annulla
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

    // -------------------------------------------------------------------------
    // I METODI DI LETTURA RESTANO INVARIATI
    // -------------------------------------------------------------------------

    private Annuncio mapRowToAnnuncio(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo_annuncio");
        Annuncio annuncio;

        if ("Vendita".equalsIgnoreCase(tipo)) {
            AnnuncioVendita av = new AnnuncioVendita();
            av.setPrezzoMedio(BigDecimal.valueOf(rs.getDouble("prezzo")));
            av.setPrezzoMinimo(BigDecimal.valueOf(rs.getDouble("prezzo_minimo")));
            annuncio = av;
        } else if ("Scambio".equalsIgnoreCase(tipo)) {
            AnnuncioScambio as = new AnnuncioScambio();
            as.setListaOggetti(rs.getString("nomi_items_scambio"));
            annuncio = as;
        } else {
            annuncio = new AnnuncioRegalo();
        }

        annuncio.setId(rs.getInt("id"));
        annuncio.setDescrizione(rs.getString("descrizione"));
        // annuncio.setOrarioInizio(...);

        Utente u = new Utente();
        u.setId(rs.getInt("utente_id"));
        annuncio.setUtenteId(u.getId()); // Corretto per usare setCreatore e non setUtenteId se non esiste

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
                    // Importante: Leggiamo anche lo stato dal DB
                    String dispStr = rs.getString("disponibilita");
                    if(dispStr != null) {
                        try {
                            obj.setDisponibilita(Oggetto.DISPONIBILITA.valueOf(dispStr.replace(" ", "_").toUpperCase()));
                        } catch (Exception e) {}
                    }
                    oggetti.add(obj);
                }
            }
        }
        return oggetti;
    }

    public Annuncio ottieniAnnuncio(int id) {
        String sql = "SELECT * FROM ANNUNCIO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Annuncio annuncio = mapRowToAnnuncio(rs);
                    annuncio.setOggetti(recuperaOggettiPerAnnuncio(conn, id));
                    return annuncio;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Annuncio OttieniAnnuncio(int id) {
        return ottieniAnnuncio(id);
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunci() {
        ArrayList<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ANNUNCIO ORDER BY id DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Annuncio annuncio = mapRowToAnnuncio(rs);
                annuncio.setOggetti(recuperaOggettiPerAnnuncio(conn, annuncio.getId()));
                lista.add(annuncio);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public ArrayList<Annuncio> OttieniAnnunciNonMiei(int idUtenteCorrente) {
        ArrayList<Annuncio> lista = new ArrayList<>();
        // Usiamo utente_id per escludere i propri annunci
        String sql = "SELECT * FROM ANNUNCIO WHERE utente_id <> ? ORDER BY id DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1. Inserisci il parametro QUI prima di eseguire
            ps.setInt(1, idUtenteCorrente);

            // 2. Esegui la query dopo aver settato i parametri
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Annuncio annuncio = mapRowToAnnuncio(rs);
                    // Recuperiamo gli oggetti associati usando la connessione esistente
                    annuncio.setOggetti(recuperaOggettiPerAnnuncio(conn, annuncio.getId()));
                    lista.add(annuncio);
                }
            }

        } catch (SQLException e) {
            System.err.println("Errore nel recupero annunci non miei: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public boolean modificaAnnuncio(Annuncio annuncio) {
        return true;
    }
}