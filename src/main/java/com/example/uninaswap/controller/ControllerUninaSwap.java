package com.example.uninaswap.controller;

import java.util.ArrayList;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.uninaswap.dao.*;
import com.example.uninaswap.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ControllerUninaSwap {
    private static ControllerUninaSwap istanziato = null;
    private UtenteDAO utenteDAO = new UtenteDAO();
    private OffertaDAO offertaDAO = new OffertaDAO();
    private AnnuncioDAO annuncioDAO = new AnnuncioDAO();
    private Utente utente;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private ControllerUninaSwap(){
    }
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
            // se non esiste nel DB allora usciamo
            if (utenteNelDB == null) return false;

            String passwordAttuale = utenteModificato.getPassword();
            System.out.println("Password attuale vale " + passwordAttuale);
            String passwordNelDB = utenteNelDB.getPassword();

            // Se la passwordattuale è uguale a quella nel DB
            if (passwordAttuale.equals(passwordNelDB)) {
                System.out.println("La password non è stata modificata (Hash identici). Non faccio nulla.");
                // Do not re-encode. It's already correct.
            }
            // CASE 2: The password is DIFFERENT. This means it is likely RAW TEXT entered by the user.
            else {
                // Check if the user accidentally typed their old password in plain text.
                if (passwordEncoder.matches(passwordAttuale, passwordNelDB)) {
                    throw new Exception("Inserire una password diversa da quella attuale.");
                }

                // It's a new, valid raw password. Hash it.
                System.out.println("Nuova password rilevata. Eseguo l'hashing.");
                String nuovoHash = passwordEncoder.encode(passwordAttuale);
                utenteModificato.setPassword(nuovoHash);
            }

            this.utente = utenteModificato;
            return utenteDAO.modificaUtente(utenteModificato);

        } catch (Exception e) {
            e.printStackTrace();
            // It is better to re-throw the exception so the Boundary can catch the specific message
            throw e;
        }
    }    public ArrayList<Utente> OttieniUtenti(){
        return null;
    }
    public boolean VerificaPrezzoAnnuncio(Offerta Offerta, Annuncio Annuncio) {
        return true;
    }
    public ArrayList<Annuncio> OttieniAnnunci(){
        return null;
    }
    public ArrayList<Offerta> OttieniOfferte() {return null;}
    public ArrayList<Offerta> OttieniLeMieOfferte(){return null;}
    public boolean SalvaOggetto(Oggetto Oggetto){
        return true;
    }
    public boolean Recensire (Utente utenteRecensore, Utente utenteRecensito){
        return true;
    }
    public boolean SalvaOggetto(Oggetto oggetto, Utente utente){
        return true;
    }
    public boolean EliminaOggetto(Oggetto oggetto, Utente utente){
        return true;
    }
    public ArrayList<Oggetto> VediIMieiOggetti(Utente utente){
        return null;
    }
    public boolean PubblicaAnnuncio(Annuncio annuncio){
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
    public void creaUtente(
            String username, String password, String matricola, String email
            ){
        try{
            password = passwordEncoder.encode(password);
            this.utente = new Utente(username, password, matricola, email);
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente Salvato");
            String dati = utente.toString();
            System.out.println(dati);
            setUtente(utente);
        }
        catch(Exception e){
            System.out.println("Errore! Utente non salvato " + e.getMessage());

        }

    }
    public void accediUtente(String email, String password) throws Exception {
        Utente utenteTrovato = utenteDAO.ottieniUtente(email);
        if (utenteTrovato == null) {
            throw new Exception("Credenziali Errate! Utente non trovato");
        }
        String passwordHashataNelDB = utenteTrovato.getPassword();
        if (checkPassword(password, passwordHashataNelDB)) {
            System.out.println("Utente accesso con successo");
            utente = utenteDAO.ottieniUtente(email);
            setUtente(utente);
        } else {
            throw new Exception("Credenziali Errate! Password non combaciano");
        }
    }
    public void registraUtente(Utente utente) throws Exception{
        try{
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente registrato");
        }
        catch(Exception e){
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
            System.out.println("Recensione salvato");
            return true;
        } else {
            System.out.println("Recensione non salvato");
            return false;
        }
    }
    //Metodo per quando si modifica l'utente
    public boolean verificaCredenzialiDuplicate(String nuovoUsername, String nuovaMatricola, String emailAttuale) {
        return utenteDAO.verificaEsistenzaAltroUtente(nuovoUsername, nuovaMatricola, emailAttuale);
    }
    //Metodo per quando si crea l'utente
    public void verificaUtenteUnico(String username, String email, String matricola) throws Exception {
            utenteDAO.verificaEsistenzaUtenteRegistrazione(username, email, matricola);
    }
}


