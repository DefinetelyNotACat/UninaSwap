package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;

public class AnnuncioRegalo extends Annuncio {

    public AnnuncioRegalo() {
        super();
    }

    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }

    public ArrayList<OffertaRegalo> getOfferteRegalo() {
        ArrayList<OffertaRegalo> listaTipizzata = new ArrayList<>();

        for (Offerta o : super.offerte) {
            if (o instanceof OffertaRegalo) {
                listaTipizzata.add((OffertaRegalo) o);
            }
        }
        return listaTipizzata;
    }

    public void aggiungiOffertaRegalo(OffertaRegalo offertaRegalo) {
        super.offerte.add(offertaRegalo);
    }

    @Override
    public String getTipoAnnuncio(){
        return "Regalo";
    }

    @Override
    public void ottieniOfferta(Offerta offerta) {
        if (offerta instanceof OffertaRegalo) {
            super.offerte.add(offerta);
        } else {
            throw new IllegalArgumentException("Si possono aggiungere solo offerte di regalo a un AnnuncioRegalo");
        }
    }
}