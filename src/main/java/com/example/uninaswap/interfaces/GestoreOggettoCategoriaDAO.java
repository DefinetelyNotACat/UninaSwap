package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Categoria;

import java.sql.Connection;
import java.util.ArrayList;

public interface GestoreOggettoCategoriaDAO {
    public boolean associaCategoria(int idOggetto, String nomeCategoria);
    public boolean rimuoviAssociazione(int idOggetto, String nomeCategoria);
    public boolean rimuoviTutteLeCategorieDiOggetto(int idOggetto, Connection connEsterna);
    public ArrayList<Categoria> ottieniCategoriePerOggetto(int idOggetto);
    public void salvaListaCategorie(Connection conn, int idOggetto, ArrayList<Categoria> categorie);
}
