package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;

import java.util.ArrayList;

public interface GestoreOffertaDAO {
    public boolean salvaOffertaVendita(OffertaVendita offertaVendita );
    public boolean modficaOfferta(OffertaVendita offertaVendita);
    public boolean eliminaOfferta(OffertaVendita offertaVendita);
    public Offerta ottieniOfferta(OffertaVendita offertaVendita);
    public ArrayList<Offerta> OttieniTutteOfferte();
}
