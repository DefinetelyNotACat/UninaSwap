package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.*;

public class UtenteDAO {
    public boolean salvaUtente(Utente utente){return true;}
    public boolean modificaUtente(Utente utente){return true;}
    public boolean eliminaUtente(Utente utente){return true;}
    public Utente ottieniUtente(int id){return null;}
    public ArrayList<Utente> ottieniTuttiUtenti(){return null;}
}
