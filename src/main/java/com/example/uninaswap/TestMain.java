package com.example.uninaswap;

import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.dao.UtenteDAO;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;

import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("--- INIZIO TEST OGGETTO DAO ---");

        // 1. Setup DAO
        OggettoDAO oggettoDAO = new OggettoDAO();
        UtenteDAO utenteDAO = new UtenteDAO();

        // 2. Setup Utente (Con controllo esistenza per evitare crash FK)
        String emailTest = "USHABALUSHA@studenti.unina.it";
        Utente utenteTest = new Utente("Mario", "ASCAUGFJH12", "N86005532", emailTest);

        // Salviamo l'utente se non esiste, altrimenti l'inserimento oggetto fallirebbe
        // Se esiste già, lo recuperiamo per avere l'ID corretto
        if (!utenteDAO.salvaUtente(utenteTest)) {
            System.out.println("Utente esistente o errore creazione, provo a recuperarlo...");
        }
        utenteTest = utenteDAO.ottieniUtente(emailTest);

        if (utenteTest == null) {
            System.err.println("❌ Errore critico: Impossibile recuperare un utente valido. Test interrotto.");
            return;
        }
        System.out.println("Utente per il test: ID " + utenteTest.getId());


        // 3. Creiamo l'Oggetto
        Oggetto nuovoOggetto = new Oggetto();
        nuovoOggetto.setNome("Libro Java Avanzato - Test Transazione");
        nuovoOggetto.setCondizione(Oggetto.CONDIZIONE.NUOVO);
        nuovoOggetto.setDisponibilita(Oggetto.DISPONIBILITA.DISPONIBILE);

        // 4. Aggiungiamo Categorie (CORRETTO BASANDOSI SULL'IMMAGINE DB)
        ArrayList<Categoria> categorie = new ArrayList<>();

        // "Informatica" esiste nel DB (riga 1)
        categorie.add(new Categoria("Informatica"));

        // "Libri" NON esisteva, l'ho corretto in "Libri di testo" (riga 3)
        categorie.add(new Categoria("Libri di testo"));

        // Ho rimosso "Universita" perché non c'è nella tua tabella.
        // Se vuoi aggiungerne una terza valida, potresti usare "Cancelleria" o "Elettronica".

        nuovoOggetto.setCategorie(categorie);

        // 5. Aggiungiamo Immagini
        ArrayList<String> immagini = new ArrayList<>();
        immagini.add("/path/to/img1.jpg");
        immagini.add("/path/to/img2.jpg");
        nuovoOggetto.setImmagini(immagini);

        System.out.println("Tentativo di salvataggio...");

        // 6. ESECUZIONE
        boolean esito = oggettoDAO.salvaOggetto(nuovoOggetto, utenteTest);

        if (esito) {
            System.out.println("✅ SUCCESSO! Oggetto salvato.");
            System.out.println("ID Generato: " + nuovoOggetto.getId());

            // 7. Rilettura per conferma
            Oggetto oggettoLetto = oggettoDAO.ottieniOggetto(nuovoOggetto.getId(), utenteTest);
            if(oggettoLetto != null) {
                System.out.println("Verifica Rilettura:");
                System.out.println("- Nome: " + oggettoLetto.getNome());
                System.out.println("- Categorie trovate: " + oggettoLetto.getCategorie().size());
                // Stampa i nomi delle categorie trovate per essere sicuri
                for(Categoria c : oggettoLetto.getCategorie()) {
                    System.out.println("  > " + c.getNome());
                }
                System.out.println("- Immagini trovate: " + oggettoLetto.getImmagini().size());
            } else {
                System.err.println("❌ ERRORE: L'oggetto risulta salvato ma non riesco a rileggerlo.");
            }

        } else {
            System.err.println("❌ FALLIMENTO: Il salvataggio ha restituito false (Controlla console per stacktrace).");
        }
    }
}