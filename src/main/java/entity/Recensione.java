package entity;

public class Recensione {

    private int id;

    private int Voto;

    private String commento;

    public Recensione(int Voto) {
        this.Voto = Voto;
    }//nn so se il commento è facoltativo

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

}
