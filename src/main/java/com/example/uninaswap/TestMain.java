package com.example.uninaswap;

import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;

import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("--- INIZIO TEST OGGETTO DAO ---");

        // 1. Setup DAO
        OggettoDAO oggettoDAO = new OggettoDAO();

        // 2. Creiamo un Utente Finto (Assicurati che l'ID 1 esista nel DB o cambialo)
        Utente utenteTest = new Utente("Mario","ASCAUGFJH12","N86005532","USHABALUSHA@studenti.unina.it");
        utenteTest.setId(1);

        // 3. Creiamo l'Oggetto
        Oggetto nuovoOggetto = new Oggetto();
        nuovoOggetto.setNome("Libro Java Avanzato - Test Transazione");
        nuovoOggetto.setCondizione(Oggetto.CONDIZIONE.NUOVO); // Assicurati di usare i tuoi ENUM corretti
        nuovoOggetto.setDisponibilita(Oggetto.DISPONIBILITA.DISPONIBILE);

        // 4. Aggiungiamo Categorie (Test Batch)
        ArrayList<Categoria> categorie = new ArrayList<>();
        categorie.add(new Categoria("Libri"));
        categorie.add(new Categoria("Informatica"));
        categorie.add(new Categoria("Universita"));
        nuovoOggetto.setCategorie(categorie);

        // 5. Aggiungiamo Immagini (Test Batch)
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
                System.out.println("- Immagini trovate: " + oggettoLetto.getImmagini().size());
            } else {
                System.err.println("❌ ERRORE: L'oggetto risulta salvato ma non riesco a rileggerlo.");
            }

        } else {
            System.err.println("❌ FALLIMENTO: Il salvataggio ha restituito false.");
        }
    }
}