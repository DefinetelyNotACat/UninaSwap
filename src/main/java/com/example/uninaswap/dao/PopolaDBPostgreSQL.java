package com.example.uninaswap.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PopolaDBPostgreSQL {

    public static void creaDB() throws Exception{
        System.out.println("--- Creazione Schema con ENUM Nativi e Pulizia Tipi ---");

        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Creazione Enum
            stmt.executeUpdate("CREATE TYPE stato_annuncio AS ENUM ('DISPONIBILE', 'NONDISPONIBILE');");
            stmt.executeUpdate("CREATE TYPE condizione_oggetto AS ENUM ('NUOVO', 'COME_NUOVO', 'OTTIME_CONDIZIONI', 'BUONE_CONDIZIONI', 'DISCRETE_CONDIZIONI', 'CATTIVE_CONDIZIONI');");
            stmt.executeUpdate("CREATE TYPE disponibilita_oggetto AS ENUM ('DISPONIBILE', 'OCCUPATO', 'VENDUTO', 'REGALATO', 'SCAMBIATO');");
            stmt.executeUpdate("CREATE TYPE stato_offerta AS ENUM ('IN_ATTESA', 'ACCETTATA', 'RIFIUTATA');");

            System.out.println("TIPI ENUM CREATI.");

            // MODIFICA: Aggiunto ID numerico, matricola diventa UNIQUE
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

            String querySede = "CREATE TABLE SEDE (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "nome_sede VARCHAR(100), " +
                    "indirizzo VARCHAR(255)" +
                    ");";
            stmt.executeUpdate(querySede);
            System.out.println("Tabella SEDE CREATA");

            String queryCategoria = "CREATE TABLE CATEGORIA (" +
                    "nome VARCHAR(50) PRIMARY KEY" +
                    ");";
            stmt.executeUpdate(queryCategoria);
            System.out.println("Tabella CATEGORIA CREATA");

            // MODIFICA: FK punta a utente_id (INTEGER)
            String queryAnnuncio = "CREATE TABLE ANNUNCIO (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "utente_id INTEGER NOT NULL, " +
                    "sede_id INTEGER NOT NULL, " +
                    "tipo_annuncio VARCHAR(20) NOT NULL, " +
                    "stato stato_annuncio, " +
                    "descrizione TEXT, " +
                    "orario_inizio TIME, " +
                    "orario_fine TIME, " +
                    "prezzo DOUBLE PRECISION, " +
                    "prezzo_minimo DOUBLE PRECISION, " +
                    "nomi_items_scambio TEXT, " +
                    "CONSTRAINT fk_utente_annuncio FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_sede_annuncio FOREIGN KEY (sede_id) REFERENCES SEDE(id) ON DELETE SET NULL" +
                    ");";
            stmt.executeUpdate(queryAnnuncio);
            System.out.println("Tabella ANNUNCIO CREATA");

            String queryOggetto = "CREATE TABLE OGGETTO (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "annuncio_id INTEGER NOT NULL, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "descrizione TEXT, " +
                    "condizione condizione_oggetto, " +
                    "disponibilita disponibilita_oggetto, " +
                    "immagine_path TEXT, " +
                    "CONSTRAINT fk_annuncio_oggetto FOREIGN KEY (annuncio_id) REFERENCES ANNUNCIO(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryOggetto);
            System.out.println("Tabella OGGETTO CREATA");

            String queryOggettoCategoria = "CREATE TABLE OGGETTO_CATEGORIA (" +
                    "oggetto_id INTEGER NOT NULL, " +
                    "categoria_nome VARCHAR(50) NOT NULL, " +
                    "PRIMARY KEY (oggetto_id, categoria_nome), " +
                    "CONSTRAINT fk_objcat_oggetto FOREIGN KEY (oggetto_id) REFERENCES OGGETTO(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_objcat_categoria FOREIGN KEY (categoria_nome) REFERENCES CATEGORIA(nome) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryOggettoCategoria);
            System.out.println("Tabella OGGETTO_CATEGORIA CREATA");

            // MODIFICA: FK punta a utente_id (INTEGER)
            String queryOfferta = "CREATE TABLE OFFERTA (" +
                    "id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "utente_id INTEGER NOT NULL, " +
                    "annuncio_id INTEGER NOT NULL, " +
                    "tipo_offerta VARCHAR(20), " +
                    "messaggio TEXT, " +
                    "stato stato_offerta, " +
                    "orario_inizio TIME, " +
                    "orario_fine TIME, " +
                    "prezzo_offerta DOUBLE PRECISION, " +
                    "CONSTRAINT fk_utente_offerta FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_annuncio_offerta FOREIGN KEY (annuncio_id) REFERENCES ANNUNCIO(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(queryOfferta);
            System.out.println("Tabella OFFERTA CREATA");

            // MODIFICA: FK puntano a recensore_id e recensito_id (INTEGER)
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

            System.out.println("Database completato");

        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cancellaDB(){
        try(Connection conn = PostgreSQLConnection.getConnection();
            Statement stmt = conn.createStatement();){

            // Ordine di cancellazione: dalle tabelle figlie alle tabelle padri
            stmt.executeUpdate("DROP TABLE IF EXISTS RECENSIONE CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS OFFERTA CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS OGGETTO_CATEGORIA CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS OGGETTO CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS ANNUNCIO CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS CATEGORIA CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS SEDE CASCADE;");
            stmt.executeUpdate("DROP TABLE IF EXISTS UTENTE CASCADE;");

            // Cancellazione tipi enum
            stmt.executeUpdate("DROP TYPE IF EXISTS stato_annuncio CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS condizione_oggetto CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS disponibilita_oggetto CASCADE;");
            stmt.executeUpdate("DROP TYPE IF EXISTS stato_offerta CASCADE;");

            System.out.println("PULIZIA COMPLETATA.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}