package entity;
public class Utente {
    String username;
    String password;
    String matricola;
    String email;
    int id;
    public Utente(String username, String password, String matricola, String email) {
        this.username = username;
        this.password = password;
        this.matricola = matricola;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Username : " + this.username + " Email : " + this.email + " Matricola : " + this.matricola + " Password : " + this.password;
    }
}
