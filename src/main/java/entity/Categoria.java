package entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class Categoria {
    private int id;
    private String nome;

    Categoria(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String Nome) {
        this.nome = nome;
    }

}
