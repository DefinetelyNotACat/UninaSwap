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
        // SQL: Aggiorna nome, condizione e disponibilità
        String sql = "UPDATE OGGETTO SET nome = ?, condizione = ?::condizione_oggetto, disponibilita = ?::disponibilita_oggetto WHERE id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Nome
            stmt.setString(1, oggetto.getNome());

            // 2. Condizione (Usa il metodo helper per correggere il formato)
            if (oggetto.getCondizione() != null) {
                // Converte "DISCRETE_CONDIZIONI" -> "Discrete condizioni"
                String valoreCorretto = toDbEnum(oggetto.getCondizione().name());
                stmt.setString(2, valoreCorretto);
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            // 3. Disponibilità (Usa il metodo helper per correggere il formato)
            if (oggetto.getDisponibilita() != null) {
                // Converte "DISPONIBILE" -> "Disponibile" (se necessario)
                String valoreCorretto = toDbEnum(oggetto.getDisponibilita().name());
                stmt.setString(3, valoreCorretto);
            } else {
                stmt.setString(3, "DISPONIBILE");
            }

            // 4. ID Oggetto
            stmt.setInt(4, oggetto.getId());

            // Esegui l'aggiornamento
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Oggetto aggiornato con successo nel DB!");
                return true;
            } else {
                System.err.println("Errore: Nessun oggetto trovato con ID " + oggetto.getId());
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Controlla la console se vedi errori qui!
            return false;
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
    }    public boolean associaUtente(int idUtente, int idOggetto) { return true; }
    public boolean rimuoviDaUtente(int idUtente, int idOggetto) { return true; }
    public boolean associaAnnuncio(int idUtente, int idAnnuncio) { return true; }
    public boolean rimuoviDaAnnuncio(int idUtente, int idAannuncio) { return true; }


}