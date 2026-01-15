package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreAnnuncioDAO;
import com.example.uninaswap.entity.Oggetto.DISPONIBILITA;

public class AnnuncioDAO implements GestoreAnnuncioDAO {

    /**
     * MOTORE DI RICERCA INTERNO (JOIN)
     * Recupera Annunci, Sede, Oggetti e Immagini in un'unica query per massime prestazioni.
     * Questa logica raggruppa correttamente le righe multiple restituite dalla JOIN.
     */
    private ArrayList<Annuncio> caricaAnnunciConJoin(String condizioneSql, Object... params) {
        LinkedHashMap<Integer, Annuncio> mappaAnnunci = new LinkedHashMap<>();

        String sql = "SELECT a.*, s.nome_sede, o.id as o_id, o.nome as o_nome, i.path as i_path " +
                "FROM ANNUNCIO a " +
                "LEFT JOIN SEDE s ON a.sede_id = s.id " +
                "LEFT JOIN OGGETTO o ON a.id = o.annuncio_id " +
                "LEFT JOIN IMMAGINE i ON o.id = i.oggetto_id " +
                condizioneSql;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Binding dinamico dei parametri
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idAnnuncio = rs.getInt("id");

                    // 1. Raggruppamento Annuncio: se non esiste nella mappa, lo creiamo
                    Annuncio annuncio = mappaAnnunci.get(idAnnuncio);
                    if (annuncio == null) {
                        annuncio = mapRowToAnnuncio(rs);
                        Sede sede = new Sede();
                        sede.setNomeSede(rs.getString("nome_sede"));
                        annuncio.setSede(sede);
                        mappaAnnunci.put(idAnnuncio, annuncio);
                    }

                    // 2. Raggruppamento Oggetti: verifichiamo se l'oggetto è già presente nella lista dell'annuncio
                    int idOggetto = rs.getInt("o_id");
                    if (idOggetto > 0) {
                        final int currentObjId = idOggetto;
                        Oggetto oggetto = annuncio.getOggetti().stream()
                                .filter(obj -> obj.getId() == currentObjId)
                                .findFirst().orElse(null);

                        if (oggetto == null) {
                            oggetto = new Oggetto();
                            oggetto.setId(idOggetto);
                            oggetto.setNome(rs.getString("o_nome"));
                            annuncio.getOggetti().add(oggetto);
                        }

                        // 3. Raggruppamento Immagini: aggiungiamo il path alla lista immagini dell'oggetto
                        String pathImmagine = rs.getString("i_path");
                        if (pathImmagine != null && !oggetto.getImmagini().contains(pathImmagine)) {
                            oggetto.getImmagini().add(pathImmagine);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento annunci: " + e.getMessage());
        }
        return new ArrayList<>(mappaAnnunci.values());
    }

    // --- METODI DI INTERFACCIA (Implementazione tramite caricaAnnunciConJoin) ---

    public ArrayList<Annuncio> OttieniAnnunciNonMiei(int idUtenteCorrente) {
        return caricaAnnunciConJoin("WHERE a.utente_id <> ? ORDER BY a.id DESC", idUtenteCorrente);
    }

    public ArrayList<Annuncio> OttieniAnnunciRicercaUtente(String ricerca, int mioId) {
        return caricaAnnunciConJoin("WHERE a.descrizione ILIKE ? AND a.utente_id <> ? ORDER BY a.id DESC",
                "%" + ricerca + "%", mioId);
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunci() {
        return caricaAnnunciConJoin("ORDER BY a.id DESC");
    }

    @Override
    public Annuncio OttieniAnnuncio(int id) {
        ArrayList<Annuncio> res = caricaAnnunciConJoin("WHERE a.id = ?", id);
        return res.isEmpty() ? null : res.get(0);
    }

    // --- LOGICA DI SCRITTURA (TRANSAZIONALE) ---

    public boolean inserisciAnnuncio(Annuncio annuncio, int utenteId) {
        String sql = "INSERT INTO ANNUNCIO (utente_id, sede_id, tipo_annuncio, descrizione, orario_inizio, orario_fine, prezzo, prezzo_minimo, nomi_items_scambio, stato) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::stato_annuncio)";

        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, utenteId);
                if(annuncio.getSede() != null) ps.setInt(2, annuncio.getSede().getId());
                else ps.setNull(2, Types.INTEGER);

                ps.setString(3, annuncio.getTipoAnnuncio());
                ps.setString(4, annuncio.getDescrizione());
                ps.setObject(5, annuncio.getOrarioInizio());
                ps.setObject(6, annuncio.getOrarioFine());

                if (annuncio instanceof AnnuncioVendita av) {
                    ps.setDouble(7, av.getPrezzoMedio() != null ? av.getPrezzoMedio().doubleValue() : 0.0);
                    ps.setDouble(8, av.getPrezzoMinimo() != null ? av.getPrezzoMinimo().doubleValue() : 0.0);
                    ps.setNull(9, Types.VARCHAR);
                } else if (annuncio instanceof AnnuncioScambio as) {
                    ps.setNull(7, Types.DOUBLE); ps.setNull(8, Types.DOUBLE);
                    ps.setString(9, as.getListaOggetti());
                } else {
                    ps.setNull(7, Types.DOUBLE); ps.setNull(8, Types.DOUBLE); ps.setNull(9, Types.VARCHAR);
                }
                ps.setString(10, "DISPONIBILE");

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        annuncio.setId(id);
                        associaOggettiAdAnnuncio(conn, id, annuncio.getOggetti());
                    }
                }

                // Aggiorniamo la disponibilità in memoria
                if (annuncio.getOggetti() != null) {
                    for(Oggetto o : annuncio.getOggetti()) o.setDisponibilita(DISPONIBILITA.OCCUPATO);
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void associaOggettiAdAnnuncio(Connection conn, int annuncioId, ArrayList<Oggetto> oggetti) throws SQLException {
        String sql = "UPDATE OGGETTO SET annuncio_id = ?, disponibilita = 'OCCUPATO'::disponibilita_oggetto WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (oggetti != null) {
                for (Oggetto o : oggetti) {
                    ps.setInt(1, annuncioId);
                    ps.setInt(2, o.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public boolean eliminaAnnuncio(int id) {
        String sqlLibera = "UPDATE OGGETTO SET annuncio_id = NULL, disponibilita = 'DISPONIBILE'::disponibilita_oggetto WHERE annuncio_id = ?";
        String sqlDel = "DELETE FROM ANNUNCIO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlLibera);
                 PreparedStatement ps2 = conn.prepareStatement(sqlDel)) {
                ps1.setInt(1, id); ps1.executeUpdate();
                ps2.setInt(1, id); ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- HELPER DI MAPPATURA ---

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
        annuncio.setUtenteId(rs.getInt("utente_id"));

        return annuncio;
    }

    @Override
    public boolean modificaAnnuncio(Annuncio annuncio) {
        return true; // Placeholder richiesto
    }
    public ArrayList<Annuncio> OttieniAnnunciDiUtente(int idUtente) {
        return caricaAnnunciConJoin("WHERE a.utente_id = ? ORDER BY a.id DESC", idUtente);
    }
}