package com.example.uninaswap.controller;

import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.uninaswap.dao.*;
import com.example.uninaswap.entity.*;

public class ControllerUninaSwap {

    //Attributi
    //
    private static ControllerUninaSwap istanziato = null;
    private UtenteDAO utenteDAO = new UtenteDAO();
    private OffertaDAO offertaDAO = new OffertaDAO();
    private AnnuncioDAO annuncioDAO = new AnnuncioDAO();
    private OggettoDAO oggettoDAO = new OggettoDAO();
    private Utente utente;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    //Costruttore
    //
    private ControllerUninaSwap(){}

    //Metodi
    //
    public static ControllerUninaSwap getInstance() {
            if (istanziato == null) {
                istanziato = new ControllerUninaSwap();
            }
            return istanziato;
    }

    public void setUtente(Utente utente) {
            this.utente = utente;
    }
    public Utente getUtente() throws Exception{
            if(this.utente != null) {
                return this.utente;
            }
            throw new Exception("Utente non registrato!");
    }
    public void creaUtente(String username, String password, String matricola, String email){
        try{
            email = email.toLowerCase().trim();
            password = passwordEncoder.encode(password);
            this.utente = new Utente(username, password, matricola, email);
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente Salvato");
            setUtente(utente);
        } catch(Exception exception){
            System.out.println("Errore! Utente non salvato " + exception.getMessage());
        }
    }
    public boolean ModificaUtente(Utente utenteModificato) throws Exception {
        try {
            Utente utenteNelDB = utenteDAO.ottieniUtente(utenteModificato.getEmail());
            if (utenteNelDB == null) return false;
            String passwordAttuale = utenteModificato.getPassword();
            String passwordNelDB = utenteNelDB.getPassword();
            if (passwordAttuale.equals(passwordNelDB)) {
                System.out.println("La password non è stata modificata (Hash identici). Non faccio nulla.");
            } else {
                if (passwordEncoder.matches(passwordAttuale, passwordNelDB)) {
                    throw new Exception("Inserire una password diversa da quella attuale.");
                }
                System.out.println("Nuova password rilevata. Eseguo l'hashing.");
                String nuovoHash = passwordEncoder.encode(passwordAttuale);
                utenteModificato.setPassword(nuovoHash);
            }
            this.utente = utenteModificato;
            return utenteDAO.modificaUtente(utenteModificato);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
    public void verificaUtenteUnico(String username, String email, String matricola) throws Exception {
        utenteDAO.verificaEsistenzaUtenteRegistrazione(username, email, matricola);
    }
    public ArrayList<Utente> cercaUtenti(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Controlliamo se c'è un utente loggato
        int mioId = -1;
        if (this.utente != null) {
            mioId = this.utente.getId();
        }
        // Passiamo la query e il MIO id al DAO
        return utenteDAO.cercaUtentiByUsername(query, mioId);
    }
    public void accediUtente(String email, String password) throws Exception {
        email = email.toLowerCase().trim();
        Utente utenteTrovato = utenteDAO.ottieniUtente(email);
        if (utenteTrovato == null) {
            throw new Exception("Credenziali Errate! Utente non trovato");
        }
        String passwordHashataNelDB = utenteTrovato.getPassword();
        if (checkPassword(password, passwordHashataNelDB)) {
            System.out.println("Utente accesso con successo");
            this.utente = utenteTrovato;
        } else {
            throw new Exception("Credenziali Errate! Le Password non combaciano");
        }
    }
    public boolean checkPassword(String password, String passwordHashata) {
        return passwordEncoder.matches(password, passwordHashata);
    }
    public ArrayList<Annuncio> OttieniAnnunciDiUtente(int idUtente) {
        return annuncioDAO.OttieniAnnunciDiUtente(idUtente);
    }
    public ArrayList<Annuncio> OttieniAnnunciDiUtenteDaAltroUtente(int idUtente) {
        return annuncioDAO.OttieniAnnunciDiUtenteDaALtroUtente(idUtente);
    }
    public Utente ottieniUtenteDaEmail(String email) {
        try {
            return utenteDAO.ottieniUtente(email);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    public ArrayList<Recensione> OttieniRecensioniRicevuteUtente(Utente utente) {
        if (utente == null) return new ArrayList<>();
        RecensioneDAO dao = new RecensioneDAO();
        return dao.ottieniRecensioniPerUtente(utente.getEmail());
    }
    public ArrayList<Recensione> OttieniRecensioniFatteUtente(Utente utente) {
        if (utente == null) return new ArrayList<>();
        RecensioneDAO dao = new RecensioneDAO();
        return dao.ottieniRecensioniFatteDaUtente(utente.getEmail());
    }

    public boolean PubblicaAnnuncio(Annuncio annuncio){
        System.out.println("Annuncio pubblicato "  + annuncio.toString());
        AnnuncioDAO annuncioDAO = new AnnuncioDAO();
        annuncioDAO.inserisciAnnuncio(annuncio, this.utente.getId());
        return true;
    }
    public boolean EliminaAnnuncio(Annuncio annuncio){
        return annuncioDAO.eliminaAnnuncio(annuncio.getId());
    }
    public ArrayList<Annuncio> FiltraAnnunciCatalogo(String testo, Oggetto.CONDIZIONE condizione, Categoria categoria) {
        try {
            int mioId = (this.utente != null) ? this.utente.getId() : -1;
            String condStr = (condizione != null) ? condizione.name() : null;
            String catStr = (categoria != null) ? categoria.getNome() : null;
            return annuncioDAO.OttieniAnnunciFiltrati(testo, condStr, catStr, mioId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<Offerta> OttieniLeMieOfferte() {
        if (this.utente == null) return new ArrayList<>();
        //Offerte che IO ho inviato ad altri
        return offertaDAO.ottieniOfferteInviate(this.utente.getId());
    }
    public ArrayList<Offerta> OttieniOfferteRicevute() {
        if (this.utente == null) return new ArrayList<>();
        //Offerte che ALTRI hanno inviato ai MIEI annunci
        return offertaDAO.ottieniOfferteRicevute(this.utente.getId());
    }
    public boolean EseguiOfferta(Utente utente, Offerta offerta) throws Exception {
        //Controllo se l'utente è il proprietario dell'annuncio (non puoi farti un'offerta da solo)
        if (offerta.getAnnuncio().getUtenteId() == utente.getId()) {
            throw new Exception("Non puoi fare un'offerta sul tuo stesso annuncio!");
        }
        //Controllo se esiste già un'offerta IN_ATTESA [NUOVA LOGICA]
        if (offertaDAO.haOffertaInAttesa(utente.getId(), offerta.getAnnuncio().getId())) {
            throw new Exception("Hai già un'offerta in attesa per questo annuncio. Attendi la risposta del venditore.");
        }
        System.out.println("Controller: Salvataggio offerta per l'annuncio " + offerta.getAnnuncio().getId());
        return offertaDAO.salvaOfferta(offerta);
    }
    public boolean GestisciStatoOfferta(Offerta offerta, Offerta.STATO_OFFERTA nuovoStato) {
        //Aggiorniamo lo stato dell'offerta nel DB
        boolean esito = offertaDAO.modificaStatoOfferta(offerta.getId(), nuovoStato);
        if (esito) {
            offerta.setStato(nuovoStato);

            //Se l'offerta viene ACCETTATA, l'annuncio deve diventare NON_DISPONIBILE
            if (nuovoStato == Offerta.STATO_OFFERTA.ACCETTATA) {
                int idAnnuncioReal = offerta.getAnnuncio().getId();
                boolean annuncioChiuso = annuncioDAO.aggiornaStatoAnnuncio(idAnnuncioReal, "NON_DISPONIBILE");

                if (annuncioChiuso) {
                    System.out.println("Annuncio ID " + idAnnuncioReal + " marcato come NON_DISPONIBILE.");
                }
            }
        }
        return esito;
    }

    public boolean ModificaOggetto(Oggetto oggetto) {
        return oggettoDAO.modificaOggetto(oggetto);
    }
    public boolean SalvaOggetto(Oggetto oggetto, Utente utente){
        return oggettoDAO.salvaOggetto(oggetto, utente);
    }
    public boolean EliminaOggetto(Oggetto oggetto, Utente utente) {
        return oggettoDAO.eliminaOggetto(oggetto.getId());
    }
    public ArrayList<Oggetto> OttieniOggetti(Utente utente){
        if (utente == null) return new ArrayList<>();
        return (ArrayList<Oggetto>) oggettoDAO.ottieniTuttiOggetti(utente.getId());
    }
    public ArrayList<Oggetto> OttieniOggettiDisponibili(Utente utente){
        if (utente == null) return new ArrayList<>();
        return (ArrayList<Oggetto>) oggettoDAO.ottieniTuttiOggettiDisponibili(utente.getId());
    }

    public void popolaDB() throws Exception {
        PopolaDBPostgreSQL.creaDB();
    }
    public void cancellaDB(){
        PopolaDBPostgreSQL.cancellaDB();
    }
    public boolean verificaCredenzialiDuplicate(String nuovoUsername, String nuovaMatricola, String emailAttuale) {
        return utenteDAO.verificaEsistenzaAltroUtente(nuovoUsername, nuovaMatricola, emailAttuale);
    }

    public ArrayList<Categoria> getCategorie() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        return categoriaDAO.OttieniCategorie();
    }
    public ArrayList<Oggetto.CONDIZIONE> getCondizioni() {
        CondizioneDAO condizioneDAO = new CondizioneDAO();
        return condizioneDAO.ottieniTutteCondizioni();
    }
    public ArrayList <Sede> getSedi(){
        SedeDAO sediDAO = new SedeDAO();
        return sediDAO.OttieniSedi();
    }

    public boolean pubblicaRecensione(Utente recensito, Utente recensore, int voto, String commento) {
        RecensioneDAO dao = new RecensioneDAO();

        //Cerchiamo se esiste già una recensione tra l'utente loggato (recensore) e il target (recensito)
        Recensione esistente = dao.OttieniRecensioneTraUtenti(recensore.getEmail(), recensito.getEmail());

        if (esistente != null) {
            // Se esiste, aggiorniamo i dati dell'oggetto trovato (che ha già l'ID corretto)
            System.out.println("DEBUG CONTROLLER: Trovata recensione esistente (ID: " + esistente.getId() + "). Procedo con UPDATE.");
            esistente.setVoto(voto);
            esistente.setCommento(commento);
            return dao.ModificaRecensione(esistente);
        } else {
            // Se non esiste, creiamo una nuova recensione
            System.out.println("DEBUG CONTROLLER: Nessuna recensione trovata. Procedo con INSERT.");
            Recensione nuova = new Recensione(recensito.getEmail(), recensore.getEmail(), voto);
            nuova.setCommento(commento);
            return dao.SalvaRecensione(nuova);
        }
    }
    public Recensione trovaRecensioneEsistente(Utente recensore, Utente recensito) {
        return new RecensioneDAO().OttieniRecensioneTraUtenti(recensore.getEmail(), recensito.getEmail());
    }

}
