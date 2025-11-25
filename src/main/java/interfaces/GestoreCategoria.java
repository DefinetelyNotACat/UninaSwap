package interfaces;

import entity.Categoria;
import entity.Sede;

import java.util.ArrayList;

public interface GestoreCategoria {
    public boolean salvaCategoria(Categoria categoria);
    public boolean modificaCategoria(Categoria categoria);
    public boolean eliminaCategoria(String nome);
    public Categoria OttieniCategoria(String nome);
    public ArrayList<Categoria> OttieniCategorie();
}
