package com.example.uninaswap.controller;

import com.example.uninaswap.dao.*;
import com.example.uninaswap.entity.*;

import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    public boolean ModificaUtente(Utente Utente) {
        return true;
    }
    public ArrayList<Utente> OttieniUtenti(){
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
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    public ArrayList<Offerta> LeMieOfferte(Utente utente){return null;}
    public ArrayList<Offerta> OfferteRicevuteAnnuncio(){return null;}
    public void creaUtente(
            String username, String password, String matricola, String email, String pathImmagineSelezionata
            ){
        try{
            password = passwordEncoder.encode(password);
            this.utente = new Utente(username, password, matricola, email);
            if(pathImmagineSelezionata != null){
                utente.setPathImmagineProfilo(pathImmagineSelezionata);
            }
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
    public void registraUtente(Utente utente){
        try{
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente registrato");
        }
        catch(Exception e){
            System.out.println("Errore! Utente non salvato " + e.getMessage());
        }

    }
    public String hashPassword(String password){
        return passwordEncoder.encode(password);
    }
    public boolean checkPassword(String password, String passwordHashata) {
        return passwordEncoder.matches(password, passwordHashata);
    }
    public void popolaDB(){
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
}

