package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Categoria;

import java.util.ArrayList;

public interface GestoreCategoriaDAO {
    public boolean salvaCategoria(Categoria categoria);
    public boolean modificaCategoria(Categoria categoria);
    public boolean eliminaCategoria(String nome);
    public Categoria OttieniCategoria(String nome);
    public ArrayList<Categoria> OttieniCategorie();
}
