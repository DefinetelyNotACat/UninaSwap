package Controller;

import entity.Annuncio;
import entity.Offerta;
import entity.Utente;
import java.io.IOException;
import java.util.ArrayList;

public class ControllerUninaSwap {
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
}
