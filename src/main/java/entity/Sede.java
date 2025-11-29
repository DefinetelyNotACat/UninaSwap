package entity;

public class Sede {
    private int id;
    private String nomeSede;
    private String indirizo;

    public Sede(String nomeSede, String indirizo) {
        this.nomeSede = nomeSede;
        this.indirizo = indirizo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeSede() {
        return nomeSede;
    }
    public void setNomeSede(String nomeSede) {
        this.nomeSede = nomeSede;
    }
    public String getIndirizo() {
        return indirizo;
    }
    public void setIndirizo(String indirizo) {
        this.indirizo = indirizo;
    }
}
