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
        OggettoDAO oggettoDAO = new OggettoDAO();
        UtenteDAO utenteDAO = new UtenteDAO();
        String emailTest = "USHABALUSHA@studenti.unina.it";
        Utente utenteTest = new Utente("Mario", "ASCAUGFJH12", "N86005532", emailTest);
        if (!utenteDAO.salvaUtente(utenteTest)) {
            System.out.println("Utente esistente o errore creazione, provo a recuperarlo...");
        }
        utenteTest = utenteDAO.ottieniUtente(emailTest);

        if (utenteTest == null) {
            System.err.println("❌ Errore critico: Impossibile recuperare un utente valido. Test interrotto.");
            return;
        }
        System.out.println("Utente per il test: ID " + utenteTest.getId());

        Oggetto nuovoOggetto = new Oggetto();
        nuovoOggetto.setNome("Libro Java Avanzato - Test Transazione");
        nuovoOggetto.setCondizione(Oggetto.CONDIZIONE.NUOVO);
        nuovoOggetto.setDisponibilita(Oggetto.DISPONIBILITA.DISPONIBILE);
        ArrayList<Categoria> categorie = new ArrayList<>();
        categorie.add(new Categoria("Informatica"));

        categorie.add(new Categoria("Libri di testo"));

        nuovoOggetto.setCategorie(categorie);

        // 5. Aggiungiamo Immagini
        ArrayList<String> immagini = new ArrayList<>();
        immagini.add("/path/to/img1.jpg");
        immagini.add("/path/to/img2.jpg");
        nuovoOggetto.setImmagini(immagini);

        System.out.println("Tentativo di salvataggio...");
        boolean esito = oggettoDAO.salvaOggetto(nuovoOggetto, utenteTest);

        if (esito) {
            System.out.println("✅ SUCCESSO! Oggetto salvato.");
            System.out.println("ID Generato: " + nuovoOggetto.getId());
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