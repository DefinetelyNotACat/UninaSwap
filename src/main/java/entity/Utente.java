package entity;
// TODO! AGGIUNGERE ID
public class Utente {
    String username;
    String password;
    String matricola;
    String email;
    public Utente(String username, String password, String matricola, String email) {
        this.username = username;
        this.password = password;
        this.matricola = matricola;
        this.email = email;
    }

    @Override
    public String toString() {
        String datiUtente = this.username + this.email + this.matricola + this.password;
        return datiUtente;
    }
}
