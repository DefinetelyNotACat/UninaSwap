package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PopolaDBPostgreSQL {

    public static void creaDB() throws Exception {
        System.out.println("--- Creazione Schema Finale: Relazione 1-N Oggetto-Categoria ---");

        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. ENUM
            stmt.executeUpdate("CREATE TYPE stato_annuncio AS ENUM ('DISPONIBILE', 'NONDISPONIBILE');");
            stmt.executeUpdate("CREATE TYPE condizione_oggetto AS ENUM ('NUOVO', 'COME_NUOVO', 'OTTIME_CONDIZIONI', 'BUONE_CONDIZIONI', 'DISCRETE_CONDIZIONI', 'CATTIVE_CONDIZIONI');");
            stmt.executeUpdate("CREATE TYPE disponibilita_oggetto AS ENUM ('DISPONIBILE', 'OCCUPATO', 'VENDUTO', 'REGALATO', 'SCAMBIATO');");
            stmt.executeUpdate("CREATE TYPE stato_offerta AS ENUM ('IN_ATTESA', 'ACCETTATA', 'RIFIUTATA');");
            System.out.println("TIPI ENUM CREATI.");

            // 2. UTENTE
            String queryUtente = "CREATE TABLE UTENTE (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "matricola VARCHAR(20) UNIQUE NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "immagine_profilo TEXT " +
                    ");";
            stmt.executeUpdate(queryUtente);
            System.out.println("Tabella UTENTE CREATA");

            // 3. SEDE
            String querySede = "CREATE TABLE SEDE (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "nome_sede VARCHAR(100), " +
                    "indirizzo VARCHAR(255)" +
                    ");";
            stmt.executeUpdate(querySede);
            System.out.println("Tabella SEDE CREATA");

            // 4. CATEGORIA (Deve essere creata PRIMA di OGGETTO)
            String queryCategoria = "CREATE TABLE CATEGORIA (" +
                    "nome VARCHAR(50) PRIMARY KEY" +
                    ");";
            stmt.executeUpdate(queryCategoria);
            System.out.println("Tabella CATEGORIA CREATA");

            // 5. ANNUNCIO
            String queryAnnuncio = "CREATE TABLE ANNUNCIO (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "utente_id INTEGER NOT NULL, " +
                    "sede_id INTEGER NOT NULL, " +
                    "tipo_annuncio VARCHAR(20) NOT NULL, " +
                    "stato stato_annuncio DEFAULT 'DISPONIBILE', " +
                    "descrizione TEXT, " +
                    "orario_inizio TIME, " +
                    "orario_fine TIME, " +
                    "prezzo DOUBLE PRECISION, " +
                    "prezzo_minimo DOUBLE PRECISION, " +
                    "nomi_items_scambio TEXT, " +
                    "data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_utente_annuncio FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_sede_annuncio FOREIGN KEY (sede_id) REFERENCES SEDE(id) ON DELETE SET NULL" +
                    ");";
            stmt.executeUpdate(queryAnnuncio);
            System.out.println("Tabella ANNUNCIO CREATA");

            // 6. OGGETTO (Modificata: Aggiunta categoria_nome)
            String queryOggetto = "CREATE TABLE OGGETTO (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "annuncio_id INTEGER, " +
                    "utente_id INTEGER NOT NULL, " +
                    "categoria_nome VARCHAR(50), " + // <--- NUOVA COLONNA (FK)
                    "nome VARCHAR(100) NOT NULL, " +
                    "condizione condizione_oggetto, " +
                    "disponibilita disponibilita_oggetto DEFAULT 'DISPONIBILE', " +
                    "CONSTRAINT fk_annuncio_oggetto FOREIGN KEY (annuncio_id) REFERENCES ANNUNCIO(id) ON DELETE SET NULL, " +
                    "CONSTRAINT fk_utente_oggetto FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_categoria_oggetto FOREIGN KEY (categoria_nome) REFERENCES CATEGORIA(nome) ON DELETE SET NULL" + // <--- VINCOLO FK
                    ");";
            stmt.executeUpdate(queryOggetto);
            System.out.println("Tabella OGGETTO CREATA (Con FK Categoria)");

            // 7. IMMAGINE
            String queryImmagine = "CREATE TABLE IMMAGINE (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "oggetto_id INTEGER NOT NULL, " +
                    "path TEXT NOT NULL, " +
                    "data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_oggetto_immagine FOREIGN KEY (oggetto_id) REFERENCES OGGETTO(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryImmagine);
            System.out.println("Tabella IMMAGINE CREATA");

            // 8. OGGETTO_CATEGORIA RIMOSSA COMPLETAMENTE

            // 9. OFFERTA
            String queryOfferta = "CREATE TABLE OFFERTA (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "utente_id INTEGER NOT NULL, " +
                    "annuncio_id INTEGER NOT NULL, " +
                    "tipo_offerta VARCHAR(20), " +
                    "messaggio TEXT, " +
                    "stato stato_offerta DEFAULT 'IN_ATTESA', " +
                    "orario_inizio TIME, " +
                    "orario_fine TIME, " +
                    "prezzo_offerta DOUBLE PRECISION, " +
                    "data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_utente_offerta FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_annuncio_offerta FOREIGN KEY (annuncio_id) REFERENCES ANNUNCIO(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryOfferta);
            System.out.println("Tabella OFFERTA CREATA");

            // 10. RECENSIONE
            String queryRecensione = "CREATE TABLE RECENSIONE (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "recensore_id INTEGER NOT NULL, " +
                    "recensito_id INTEGER NOT NULL, " +
                    "voto INTEGER CHECK (voto >= 1 AND voto <= 5), " +
                    "commento TEXT, " +
                    "CONSTRAINT fk_recensore FOREIGN KEY (recensore_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_recensito FOREIGN KEY (recensito_id) REFERENCES UTENTE(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryRecensione);
            System.out.println("Tabella RECENSIONE CREATA");

            System.out.println("Database completato con successo.");

        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void cancellaDB() {
        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("--- Inizio Pulizia Database ---");

            stmt.executeUpdate("DROP TABLE IF EXISTS RECENSIONE CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS OFFERTA CASCADE;");
            // stmt.executeUpdate("DROP TABLE IF EXISTS OGGETTO_CATEGORIA CASCADE;"); // RIMOSSA
            stmt.executeUpdate("DROP TABLE IF EXISTS IMMAGINE CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS OGGETTO CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS ANNUNCIO CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS CATEGORIA CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS SEDE CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS UTENTE CASCADE;");

            stmt.executeUpdate("DROP TYPE IF EXISTS stato_annuncio CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS condizione_oggetto CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS disponibilita_oggetto CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS stato_offerta CASCADE;");

            System.out.println("PULIZIA COMPLETATA.");
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la cancellazione del DB", e);
        }
    }
}