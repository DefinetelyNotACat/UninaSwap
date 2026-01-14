package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {

    // Istanze delle DAO ausiliarie
    private OggettoCategoriaDAO oggettoCategoriaDAO = new OggettoCategoriaDAO();
    // Assumo che ImmagineDAO esista e abbia il metodo inserisciImmaginiBatch
    private ImmagineDAO immagineDAO = new ImmagineDAO();
    /**
     * +Salva Oggetto, Categorie e Immagini in un'unica Transazione.
     */
    public boolean salvaOggetto(Oggetto oggetto, Utente utente) {
        // Nota: ?::type è la sintassi di cast di PostgreSQL. Assicurati che i tipi esistano nel DB.
        String sqlOggetto = "INSERT INTO OGGETTO (utente_id, nome, condizione, disponibilita) VALUES (?, ?, ?::condizione_oggetto, ?::disponibilita_oggetto)";

        Connection conn = null;

        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // --- INIZIO TRANSAZIONE ---

            // 1. Inserimento Oggetto e recupero ID
            int idOggettoGenerato = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sqlOggetto, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, utente.getId());
                stmt.setString(2, oggetto.getNome());

                // Enum -> String per il DB (FIX: Sostituisco _ con spazio per compatibilità DB)
                if (oggetto.getCondizione() != null) {
                    stmt.setString(3, oggetto.getCondizione().name().replace("_", " "));
                } else {
                    stmt.setNull(3, Types.VARCHAR);
                }

                if (oggetto.getDisponibilita() != null) {
                    stmt.setString(4, oggetto.getDisponibilita().name().replace("_", " "));
                } else {
                    stmt.setString(4, "DISPONIBILE");
                }

                int rows = stmt.executeUpdate();
                if (rows == 0) throw new SQLException("Inserimento oggetto fallito.");

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idOggettoGenerato = generatedKeys.getInt(1);
                        oggetto.setId(idOggettoGenerato); // Aggiorniamo l'ID in memoria
                    } else {
                        throw new SQLException("Nessun ID generato per l'oggetto.");
                    }
                }
            }

            // 2. Inserimento Categorie (Tabella Ponte) usando la stessa connessione
            // FIX: Chiamata corretta al metodo che accetta (Connection, int, ArrayList)
            if (oggetto.getCategorie() != null) {
                oggettoCategoriaDAO.associaCategorie(conn, idOggettoGenerato, oggetto.getCategorie());
            }

            // 3. Inserimento Immagini usando la stessa connessione
            if (oggetto.getImmagini() != null && !oggetto.getImmagini().isEmpty()) {
                immagineDAO.inserisciImmaginiBatch(conn, idOggettoGenerato, oggetto.getImmagini());
            }

            conn.commit(); // --- CONFERMA TRANSAZIONE ---
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Eseguo Rollback per errore: " + e.getMessage());
                    conn.rollback(); // --- ANNULLA TUTTO IN CASO DI ERRORE ---
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Recupera un oggetto completo (Dati Base + Categorie + Immagini).
     */
    public Oggetto ottieniOggetto(int idOggetto, Utente utente) {
        String sql = "SELECT * FROM OGGETTO WHERE id = ?";
        Oggetto oggetto = null;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    oggetto = mapResultSetToOggetto(rs);
                    oggetto.setProprietario(utente);

                    // 1. Recupero Categorie (tramite DAO ponte)
                    ArrayList<Categoria> categorie = oggettoCategoriaDAO.ottieniCategoriePerOggetto(idOggetto);
                    oggetto.setCategorie(categorie);

                    // 2. Recupero Immagini (tramite DAO Immagine)
                    ArrayList<String> immagini = immagineDAO.ottieniImmaginiStringhe(idOggetto);
                    oggetto.setImmagini(immagini);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oggetto;
    }

    public ArrayList<Oggetto> ottieniTuttiOggetti(int idUtente) {
        ArrayList<Oggetto> lista = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE utente_id = ? ORDER BY id DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Oggetto obj = mapResultSetToOggetto(rs);

                    // Recupero immagini per visualizzazione anteprima
                    obj.setImmagini(immagineDAO.ottieniImmaginiStringhe(obj.getId()));
                    // Recupero categorie
                    obj.setCategorie(oggettoCategoriaDAO.ottieniCategoriePerOggetto(obj.getId()));

                    lista.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public ArrayList<Oggetto> ottieniTuttiOggettiDisponibili(int idUtente) {
        ArrayList<Oggetto> lista = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE utente_id = ? AND disponibilita = 'DISPONIBILE' ORDER BY id DESC";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Oggetto obj = mapResultSetToOggetto(rs);

                    // Recupero immagini per visualizzazione anteprima
                    obj.setImmagini(immagineDAO.ottieniImmaginiStringhe(obj.getId()));
                    // Recupero categorie
                    obj.setCategorie(oggettoCategoriaDAO.ottieniCategoriePerOggetto(obj.getId()));

                    lista.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminaOggetto(int idOggetto) {
        String sql = "DELETE FROM OGGETTO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper privato per mappare il ResultSet ed evitare duplicazione codice
    private Oggetto mapResultSetToOggetto(ResultSet rs) throws SQLException {
        Oggetto oggetto = new Oggetto();
        oggetto.setId(rs.getInt("id"));
        oggetto.setNome(rs.getString("nome"));

        // Enum Conversion (String DB -> Enum Java)
        // FIX: Sostituisco spazio con _ per tornare all'Enum Java
        String condStr = rs.getString("condizione");
        if(condStr != null) {
            try {
                oggetto.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr.replace(" ", "_").toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Enum non riconosciuto: " + condStr);
            }
        }

        String dispStr = rs.getString("disponibilita");
        if(dispStr != null) {
            try {
                oggetto.setDisponibilita(Oggetto.DISPONIBILITA.valueOf(dispStr.replace(" ", "_").toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Enum non riconosciuto: " + dispStr);
            }
        }
        return oggetto;
    }

    // Metodi da implementare se servono
    @Override
    public boolean modificaOggetto(Oggetto oggetto) {
        // 1. Query per i dati testuali dell'oggetto
        String sqlUpdateOggetto = "UPDATE OGGETTO SET nome = ?, condizione = ?::condizione_oggetto, disponibilita = ?::disponibilita_oggetto WHERE id = ?";

        Connection conn = null;
        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // --- INIZIO TRANSAZIONE ---

            // A. AGGIORNAMENTO DATI BASE (Nome, Condizione, Disp)
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOggetto)) {
                stmt.setString(1, oggetto.getNome());

                // Condizione
                if (oggetto.getCondizione() != null) {
                    stmt.setString(2, toDbEnum(oggetto.getCondizione().name()));
                } else {
                    stmt.setNull(2, Types.VARCHAR);
                }

                // Disponibilità
                if (oggetto.getDisponibilita() != null) {
                    stmt.setString(3, toDbEnum(oggetto.getDisponibilita().name()));
                } else {
                    stmt.setString(3, "DISPONIBILE");
                }

                stmt.setInt(4, oggetto.getId());

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Nessun oggetto trovato con ID: " + oggetto.getId());
                }
            }

            // B. GESTIONE CATEGORIE (Cancella vecchie -> Inserisci nuova)
            // 1. Cancella
            oggettoCategoriaDAO.eliminaCategoriePerOggetto(conn, oggetto.getId());

            // 2. Inserisci (se l'oggetto ha categorie selezionate)
            if (oggetto.getCategorie() != null && !oggetto.getCategorie().isEmpty()) {
                // Assicurati che il metodo associaCategorie accetti la Connection 'conn' aperta!
                oggettoCategoriaDAO.associaCategorie(conn, oggetto.getId(), oggetto.getCategorie());
            }

            // C. GESTIONE IMMAGINI (Cancella vecchie -> Inserisci nuove)
            // 1. Cancella
            immagineDAO.rimuoviImmaginiPerOggetto(conn, oggetto.getId());

            // 2. Inserisci
            if (oggetto.getImmagini() != null && !oggetto.getImmagini().isEmpty()) {
                // Assicurati che inserisciImmaginiBatch accetti la Connection 'conn' aperta!
                immagineDAO.inserisciImmaginiBatch(conn, oggetto.getId(), oggetto.getImmagini());
            }

            conn.commit(); // --- CONFERMA TUTTO ---
            System.out.println("Oggetto (Dati, Categorie, Immagini) aggiornato con successo!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            // Se qualcosa va storto, annulla tutto
            if (conn != null) {
                try {
                    System.err.println("Rollback eseguito per errore: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // --- METODO FONDAMENTALE DA AGGIUNGERE ---
    // Questo metodo trasforma "DISCRETE_CONDIZIONI" in "Discrete condizioni"
    // --- VERSIONE DEFINITIVA PER POSTGRESQL (MAIUSCOLO) ---
    private String toDbEnum(String val) {
        if (val == null) return null;

        // Esempio: "DISCRETE_CONDIZIONI" (Java) diventa "DISCRETE CONDIZIONI" (DB)
        // NON usiamo .toLowerCase() perché il tuo DB vuole il MAIUSCOLO.
        return val.replace("_", " ");
    }

    public boolean associaUtente(int idUtente, int idOggetto) { return true; }

    public boolean rimuoviDaUtente(int idUtente, int idOggetto) { return true; }

    public boolean associaAnnuncio(int idOggetto, int idAnnuncio) {
        String sql = "UPDATE OGGETTO SET annuncio_id = ? WHERE id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAnnuncio);
            stmt.setInt(2, idOggetto);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean associaOgettiAdAnnuncio(ArrayList<Oggetto> oggetti, int idAnnuncio) {
        if (oggetti == null || oggetti.isEmpty()) return false;

        boolean tuttoOk = true;

        for (Oggetto obj : oggetti) {
            // Se l'oggetto ha un ID valido, lo associamo
            if (obj.getId() > 0) {
                boolean esito = associaAnnuncio(obj.getId(), idAnnuncio);
                if (!esito) tuttoOk = false;
            }
        }
        return tuttoOk;
    }

    public void associaListaOggetti(ArrayList<Oggetto> listaOggetti, int idAnnuncio) {
        if (listaOggetti == null) return;

        for (Oggetto o : listaOggetti) {
            if (o != null && o.getId() > 0) {
                associaAnnuncio(o.getId(), idAnnuncio);
            }
        }
    }

    public boolean rimuoviDaAnnuncio(int idOggetto, int idAnnuncio) {
        String sql = "UPDATE OGGETTO SET annuncio_id = NULL WHERE id = ? AND annuncio_id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOggetto);
            stmt.setInt(2, idAnnuncio);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}