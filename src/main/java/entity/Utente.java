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
        String datiUtente = "Username : " + this.username + " Email : " + this.email + " Matricola : " + this.matricola + " Password : " + this.password;
        System.out.println(datiUtente);
        return datiUtente;
    }
}
