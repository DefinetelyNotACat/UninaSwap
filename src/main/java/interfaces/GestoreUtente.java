package interfaces;

import entity.Utente;

import java.util.ArrayList;

public interface GestoreUtente {
    public boolean salvaUtente(Utente utente);
    public boolean modificaUtente(Utente utente);
    public boolean eliminaUtente(int id);
    public Utente ottieniUtente(int id);
    public Utente ottieniUtente(String matricola);
    public ArrayList<Utente> ottieniTuttiUtenti();
}
