package Controller;

import entity.Annuncio;
import entity.Offerta;
import entity.Oggetto;
import entity.Utente;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import dao.*;
public class ControllerUninaSwap {
    UtenteDAO utenteDAO = new UtenteDAO();
    OffertaDAO offertaDAO = new OffertaDAO();
    AnnuncioDAO annuncioDAO = new AnnuncioDAO();

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    public boolean EffettuaSignIn(String Username, String Email , String Matricola,String Password) {
        return true;
    }
    public boolean VerificaLogIn(String Username, String Email , String Matricola,String Password) {
        return true;
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
    public boolean PubblicaAnnuncio(Annuncio annuncio/*int*/){
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
            Utente utente = new Utente(username, password, matricola, email);
            utenteDAO.salvaUtente(utente);
            System.out.println("Utente Salvato");
            String dati = utente.toString();
            System.out.println(dati);
        }
        catch(Exception e){
            System.out.println("Errore! Utente non salvato " + e.getMessage());
        }

    }
    public void accediUtente(String email, String password) throws Exception {
        if (email.equals(email) && checkPassword(password, passwordEncoder.encode(password))) {
            System.out.println("Utente accesso");
        } else {
            throw new Exception("Credenziali Errate!");
        }
    }
    //metodo da usare per il sign-up
    public String hashPassword(String password){
        return passwordEncoder.encode(password);
    }
    //metodo da usare per verificare il log-in
    public boolean checkPassword(String password, String passwordHashata) {
        return passwordEncoder.matches(password, passwordHashata);
    }
}
