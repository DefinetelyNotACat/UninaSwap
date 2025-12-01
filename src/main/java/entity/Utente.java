package entity;
import java.util.ArrayList;

public class Utente {
    private String username;
    private String password;
    private String matricola;
    private String email;
    private int id;
    private ArrayList<Offerta> Offerte = new ArrayList<Offerta>();
    private ArrayList<Oggetto> Oggetti = new ArrayList<Oggetto>();
    private String pathImmagineProfilo;
    public Utente(String username, String password, String matricola, String email) {
        this.username = username;
        this.password = password;
        this.matricola = matricola;
        this.email = email;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOfferta(Offerta offerta) {
        this.Offerte.add(offerta);
    }
    public ArrayList<Offerta> getOfferte() {
        return Offerte;
    }
    public void rimuoviOfferte(){
        this.Offerte.clear();
    }
    public void setPathImmagineProfilo(String pathImmagineProfilo) {
        this.pathImmagineProfilo = pathImmagineProfilo;
    }
    public String getPathImmagineProfilo() {
        return pathImmagineProfilo;
    }
    @Override
    public String toString() {
        return "Username : " + this.username + " Email : " + this.email + " Matricola : " + this.matricola + " Password : " + this.password;
    }
    public  ArrayList<Oggetto> getOggetti() {
        return Oggetti;
    }
    public void setOggetti(ArrayList<Oggetto> oggetti) {
        Oggetti = oggetti;
    }
    public void aggiungiOggetto(Oggetto oggetto){
        Oggetti.add(oggetto);
    }
}