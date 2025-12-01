package entity;

public class Recensione {

    private int id;

    private int Voto;

    private String commento;

    private Utente autore;

    private Utente destinatario;

    public Utente getAutore() {
        return autore; }

    public Utente getDestinatario() {
        return destinatario; }

    public Recensione(int Voto) {
        this.Voto = Voto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVoto(int Voto) {
        this.Voto = Voto;
    }

    public int getVoto() {
        return Voto;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getCommento() {
        return commento;
    }

    @Override
    public String toString() {
        return String.format("Voto: %d | Commento: %s (da %s)",
                Voto, commento, autore.getUsername());
    }

}
