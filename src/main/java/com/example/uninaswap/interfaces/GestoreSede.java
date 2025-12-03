package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Sede;

import java.util.ArrayList;

public interface GestoreSede {
    public boolean salvaSede(Sede sede);
    public boolean modificaSede(Sede sede);
    public boolean salvaSede(int id);
    public boolean OttieniSede(int id);
    public ArrayList<Sede> OttieniSedi();
}
