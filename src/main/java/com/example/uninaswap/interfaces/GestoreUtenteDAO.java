package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Utente;

import java.util.ArrayList;

public interface GestoreUtenteDAO {
    public boolean salvaUtente(Utente utente);
    public boolean modificaUtente(Utente utente);
    public boolean eliminaUtente(int id);
    public Utente ottieniUtente(int id);
    public Utente ottieniUtente(String matricola);
    public ArrayList<Utente> ottieniTuttiUtenti();
    public Utente trovaUtenteUsername(String username);
    public boolean verificaEsistenzaAltroUtente(String username, String matricola, String emailDaEscludere);
    public void verificaEsistenzaUtenteRegistrazione(String username, String email, String matricola) throws Exception;

}
