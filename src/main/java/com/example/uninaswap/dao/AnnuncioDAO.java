package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreAnnuncioDAO;
import com.example.uninaswap.entity.Oggetto.DISPONIBILITA;

public class AnnuncioDAO implements GestoreAnnuncioDAO {

    private ArrayList<Annuncio> caricaAnnunciConJoin(String condizioneSql, Object... params) {
        LinkedHashMap<Integer, Annuncio> mappaAnnunci = new LinkedHashMap<>();

        // Query aggiornata con JOIN su UTENTE (Mantenuta come da tua richiesta)
        String sql = "SELECT a.*, s.nome_sede, " +
                "u.username as u_username, u.matricola as u_matricola, u.email as u_email, u.immagine_profilo as u_img, " +
                "o.id as o_id, o.nome as o_nome, o.condizione as o_condizione, i.path as i_path " +
                "FROM annuncio a " +
                "LEFT JOIN sede s ON a.sede_id = s.id " +
                "LEFT JOIN utente u ON a.utente_id = u.id " +
                "LEFT JOIN oggetto o ON a.id = o.annuncio_id " +
                "LEFT JOIN immagine i ON o.id = i.oggetto_id " +
                condizioneSql;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idAnnuncio = rs.getInt("id");

                    Annuncio annuncio = mappaAnnunci.get(idAnnuncio);
                    if (annuncio == null) {
                        annuncio = mapRowToAnnuncio(rs);

                        // Popolamento SEDE
                        Sede sede = new Sede();
                        sede.setNomeSede(rs.getString("nome_sede"));
                        annuncio.setSede(sede);

                        // Popolamento UTENTE (Venditore)
                        Utente venditore = new Utente();
                        venditore.setId(rs.getInt("utente_id"));
                        venditore.setUsername(rs.getString("u_username"));
                        venditore.setMatricola(rs.getString("u_matricola"));
                        venditore.setEmail(rs.getString("u_email"));
                        venditore.setPathImmagineProfilo(rs.getString("u_img"));
                        annuncio.setUtente(venditore);

                        mappaAnnunci.put(idAnnuncio, annuncio);
                    }

                    // Logica Oggetti e Immagini
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
                            String condStr = rs.getString("o_condizione");
                            if(condStr != null) {
                                oggetto.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr.replace(" ", "_").toUpperCase()));
                            }
                            annuncio.getOggetti().add(oggetto);
                        }

                        String pathImmagine = rs.getString("i_path");
                        if (pathImmagine != null && !oggetto.getImmagini().contains(pathImmagine)) {
                            oggetto.getImmagini().add(pathImmagine);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(mappaAnnunci.values());
    }

    public ArrayList<Annuncio> OttieniAnnunciFiltrati(String ricerca, String condizione, String nomeCategoria, int mioId) {
        StringBuilder sqlFiltro = new StringBuilder("WHERE a.utente_id <> ? AND a.stato = 'DISPONIBILE'::stato_annuncio ");
        ArrayList<Object> params = new ArrayList<>();
        params.add(mioId);

        if (ricerca != null && !ricerca.trim().isEmpty()) {
            sqlFiltro.append("AND a.descrizione ILIKE ? ");
            params.add("%" + ricerca.trim() + "%");
        }

        if (condizione != null && !condizione.isEmpty()) {
            sqlFiltro.append("AND o.condizione = ?::condizione_oggetto ");
            params.add(condizione.replace("_", " "));
        }

        if (nomeCategoria != null && !nomeCategoria.isEmpty()) {
            sqlFiltro.append("AND EXISTS (")
                    .append("  SELECT 1 FROM oggetto_categoria oc ")
                    .append("  WHERE oc.oggetto_id = o.id AND oc.categoria_nome = ?")
                    .append(") ");
            params.add(nomeCategoria);
        }

        sqlFiltro.append("ORDER BY a.id DESC");
        return caricaAnnunciConJoin(sqlFiltro.toString(), params.toArray());
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunciNonMiei(int idUtenteCorrente) {
        return caricaAnnunciConJoin("WHERE a.utente_id <> ? AND a.stato = 'DISPONIBILE'::stato_annuncio ORDER BY a.id DESC", idUtenteCorrente);
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunciRicercaUtente(String ricerca, int mioId) {
        return null;
    }

    public ArrayList<Annuncio> OttieniAnnunciDiUtente(int idUtente) {
        return caricaAnnunciConJoin("WHERE a.utente_id = ? ORDER BY a.id DESC", idUtente);
    }


    public ArrayList<Annuncio> OttieniAnnunciDiUtenteDaALtroUtente(int idUtente) {
        return caricaAnnunciConJoin(
                "WHERE a.utente_id = ? AND a.stato = 'DISPONIBILE'::stato_annuncio ORDER BY a.id DESC",
                idUtente
        );
    }

    @Override
    public Annuncio OttieniAnnuncio(int id) {
        ArrayList<Annuncio> res = caricaAnnunciConJoin("WHERE a.id = ?", id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public ArrayList<Annuncio> OttieniAnnunci() {
        // MODIFICA: Aggiunto WHERE stato = 'DISPONIBILE'
        return caricaAnnunciConJoin("WHERE a.stato = 'DISPONIBILE'::stato_annuncio ORDER BY a.id DESC");
    }

    @Override
    public boolean inserisciAnnuncio(Annuncio annuncio, int utenteId) {
        String sql = "INSERT INTO annuncio (utente_id, sede_id, tipo_annuncio, descrizione, orario_inizio, orario_fine, prezzo, prezzo_minimo, nomi_items_scambio, stato) " +
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
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void associaOggettiAdAnnuncio(Connection conn, int annuncioId, ArrayList<Oggetto> oggetti) throws SQLException {
        String sql = "UPDATE oggetto SET annuncio_id = ?, disponibilita = 'OCCUPATO'::disponibilita_oggetto WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (oggetti != null) {
                for (Oggetto o : oggetti) {
                    ps.setInt(1, annuncioId);
                    ps.setInt(2, o.getId());
                    ps.addBatch();
                    o.setDisponibilita(DISPONIBILITA.OCCUPATO);
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public boolean eliminaAnnuncio(int id) {
        String sqlLibera = "UPDATE oggetto SET annuncio_id = NULL, disponibilita = 'DISPONIBILE'::disponibilita_oggetto WHERE annuncio_id = ?";
        String sqlDel = "DELETE FROM annuncio WHERE id = ?";
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

        // FIX FONDAMENTALE: Conversione da String (DB) a Enum (Java)
        String statoDalDB = rs.getString("stato");
        if (statoDalDB != null) {
            try {
                // valueOf trasforma "DISPONIBILE" in Annuncio.STATO_ANNUNCIO.DISPONIBILE
                annuncio.setStato(Annuncio.STATO_ANNUNCIO.valueOf(statoDalDB.toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                System.err.println("Stato non riconosciuto nel DB: " + statoDalDB);
                annuncio.setStato(Annuncio.STATO_ANNUNCIO.DISPONIBILE); // Fallback di sicurezza
            }
        }

        return annuncio;
    }

    @Override
    public boolean modificaAnnuncio(Annuncio annuncio) {
        return true;
    }

    public boolean aggiornaStatoAnnuncio(int idAnnuncio, String nuovoStato) {
        String sql = "UPDATE annuncio SET stato = ?::stato_annuncio WHERE id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);
            ps.setInt(2, idAnnuncio);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}