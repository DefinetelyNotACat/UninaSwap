package com.example.uninaswap.dao;

import java.util.ArrayList;

import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;

public class OffertaDAO {
    public boolean salvaOffertaVendita(OffertaVendita offertaVendita ) {return true;}
    public boolean modficaOfferta(OffertaVendita offertaVendita) {return true;}
    public boolean eliminaOfferta(OffertaVendita offertaVendita) {return true;}
    public Offerta ottieniOfferta(OffertaVendita offertaVendita) {return null;}
    public ArrayList<Offerta> OttieniTutteOfferte() {return null;}
}
