package com.example.uninaswap.controller;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.uninaswap.dao.*;
import com.example.uninaswap.entity.*;
public class ControllerUninaSwap {
    private static ControllerUninaSwap istanziato = null;
    private UtenteDAO utenteDAO = new UtenteDAO();
    private OffertaDAO offertaDAO = new OffertaDAO();
    private AnnuncioDAO annuncioDAO = new AnnuncioDAO();
    private OggettoDAO oggettoDAO = new OggettoDAO();
    private Utente utente;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private ControllerUninaSwap(){}
    public static ControllerUninaSwap getInstance() {
        if (istanziato == null) {
            istanziato = new ControllerUninaSwap();
        }
        return istanziato;
    }
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    public boolean EffettuaSignIn(String Username, String Email , String Matricola,String Password) {
        return true;
    }
    public boolean VerificaLogIn(String Username, String Email , String Matricola,String Password) {
        return true;
    }
    public Utente getUtente() throws Exception{
        if(this.utente != null) {
            return this.utente;
        }
        throw new Exception("Utente non registrato!");
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
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public ArrayList<Utente> OttieniUtenti(){
        return null;
    }
    public Utente trovaUtente(String username) throws Exception{
        return utenteDAO.trovaUtenteUsername(username);
    }
    public boolean VerificaPrezzoAnnuncio(Offerta Offerta, Annuncio Annuncio) {
        return true;
    }
    public ArrayList<Annuncio> OttieniAnnunci(){
        return annuncioDAO.OttieniAnnunci();
    }
    public ArrayList<Annuncio> OttieniAnnunciRicercaUtente(String ricerca) {
        try {
            return annuncioDAO.OttieniAnnunciRicercaUtente(ricerca, this.utente.getId());
        } catch (Exception e) {
            return annuncioDAO.OttieniAnnunciRicercaUtente(ricerca, -1);
        }
    }
    public ArrayList<Annuncio> OttieniAnnunciNonMiei(){
        return annuncioDAO.OttieniAnnunciNonMiei(this.utente.getId());
    }
    public ArrayList<Offerta> OttieniOfferte() {return null;}
    public ArrayList<Offerta> OttieniLeMieOfferte(){return null;}
    public boolean SalvaOggetto(Oggetto oggetto){
        return oggettoDAO.salvaOggetto(oggetto, utente);
    }
    public boolean ModificaOggetto(Oggetto oggetto) {
        return oggettoDAO.modificaOggetto(oggetto); //
    }
    public boolean Recensire (Utente utenteRecensore, Utente utenteRecensito){
        return true;
    }
    public boolean SalvaOggetto(Oggetto oggetto, Utente utente){
        return true;
    }
    public boolean EliminaOggetto(Oggetto oggetto, Utente utente) {
        return oggettoDAO.eliminaOggetto(oggetto.getId()); //
    }
    public ArrayList<Oggetto> OttieniOggetti(Utente utente){
        if (utente == null) return new ArrayList<>();
        return (ArrayList<Oggetto>) oggettoDAO.ottieniTuttiOggetti(utente.getId());
    }
    public ArrayList<Oggetto> OttieniOggettiDisponibili(Utente utente){
        if (utente == null) return new ArrayList<>();
        return (ArrayList<Oggetto>) oggettoDAO.ottieniTuttiOggettiDisponibili(utente.getId());
    }
    public boolean PubblicaAnnuncio(Annuncio annuncio){
        System.out.println("Annuncio pubblicato "  + annuncio.toString());
        AnnuncioDAO annuncioDAO = new AnnuncioDAO();
        annuncioDAO.inserisciAnnuncio(annuncio, this.utente.getId());
        return true;
    }
    public boolean EliminaAnnuncio(Annuncio annuncio){
        return true;
    }
    public boolean EseguiOfferta(Utente utente, Offerta offerta){
        return true;
    }
    public boolean ModificaOfferta(Offerta offerta){
        return true;
    }
    public boolean EliminaOfferta(Offerta offerta){
        return true;
    }
    public ArrayList<Offerta> LeMieOfferte(Utente utente){return null;}
    public ArrayList<Offerta> OfferteRicevuteAnnuncio(){return null;}
    public void creaUtente(String username, String password, String matricola, String email){
        try{
            email = email.toLowerCase().trim();
            password = passwordEncoder.encode(password);
            this.utente = new Utente(username, password, matricola, email);
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente Salvato");
            setUtente(utente);
        } catch(Exception e){
            System.out.println("Errore! Utente non salvato " + e.getMessage());
        }
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
            throw new Exception("Credenziali Errate! Password non combaciano");
        }
    }
    public void registraUtente(Utente utente) throws Exception{
        try{
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente registrato");
        } catch(Exception e){
            throw new Exception("Errore! Utente non salvato " + e.getMessage());
        }
    }
    public String hashPassword(String password){
        return passwordEncoder.encode(password);
    }
    public boolean checkPassword(String password, String passwordHashata) {
        return passwordEncoder.matches(password, passwordHashata);
    }
    public void popolaDB() throws Exception {
        PopolaDBPostgreSQL.creaDB();
    }
    public void cancellaDB(){
        PopolaDBPostgreSQL.cancellaDB();
    }
    public boolean pubblicaRecensione(Utente recensito, Utente recensore, int voto, String commento) {
        Recensione recensione = new Recensione(recensito.getEmail(), recensore.getEmail(), voto);
        if (commento != null) {
            recensione.setCommento(commento);
        }
        RecensioneDAO recensioneDAO = new RecensioneDAO();
        if (recensioneDAO.SalvaRecensione(recensione)) {
            recensito.addRecensioneRicevuta(recensione);
            recensore.addRecensioneInviata(recensione);
            System.out.println("Recensione salvata");
            return true;
        } else {
            System.out.println("Recensione non salvata");
            return false;
        }
    }
    public boolean verificaCredenzialiDuplicate(String nuovoUsername, String nuovaMatricola, String emailAttuale) {
        return utenteDAO.verificaEsistenzaAltroUtente(nuovoUsername, nuovaMatricola, emailAttuale);
    }
    public void verificaUtenteUnico(String username, String email, String matricola) throws Exception {
        utenteDAO.verificaEsistenzaUtenteRegistrazione(username, email, matricola);
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
}
