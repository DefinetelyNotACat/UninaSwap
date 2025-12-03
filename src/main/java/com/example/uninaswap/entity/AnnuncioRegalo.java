package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class AnnuncioRegalo extends Annuncio {

    // Costruttore
    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }

    // Metodo specifico per ottenere solo le offerte di tipo Regalo (con cast sicuro)
    public ArrayList<OffertaRegalo> getOfferteRegalo() {
        ArrayList<OffertaRegalo> listaTipizzata = new ArrayList<>();

        // Itera sulla lista generica del padre (super.offerte)
        for (Offerta o : super.offerte) {
            if (o instanceof OffertaRegalo) {
                listaTipizzata.add((OffertaRegalo) o);
            }
        }
        return listaTipizzata;
    }

    // Metodo specifico per aggiungere direttamente un'OffertaRegalo
    public void aggiungiOffertaRegalo(OffertaRegalo offertaRegalo) {
        super.offerte.add(offertaRegalo);
    }

    // Override del metodo generico padre
    @Override
    public void ottieniOfferta(Offerta offerta) {
        // Controllo di tipo: accetta solo OffertaRegalo
        if (offerta instanceof OffertaRegalo) {
            super.offerte.add(offerta);
        } else {
            throw new IllegalArgumentException("Si possono aggiungere solo offerte di regalo a un AnnuncioRegalo");
        }
    }
}