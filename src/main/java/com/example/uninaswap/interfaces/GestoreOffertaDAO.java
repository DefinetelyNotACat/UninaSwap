package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;

import java.util.ArrayList;

public interface GestoreOffertaDAO {
    public boolean salvaOfferta(Offerta offerta);
    public boolean modificaStatoOfferta(int idOfferta, Offerta.STATO_OFFERTA nuovoStato);
}
