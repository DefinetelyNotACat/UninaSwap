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
        ArrayList<OffertaRegalo> listaRegali= new ArrayList<>();

        for (Offerta offerta : super.offerte) {
            //controllo che si tratti di un Offertaregalo
            if (offerta instanceof OffertaRegalo) {
                listaRegali.add((OffertaRegalo) offerta);
            }
        }
        return listaRegali;
    }

    //override del metodo della classe Annuncio per specificare che è un Annuncioregalo
    @Override
    public String getTipoAnnuncio(){
        return "Regalo";
    }

    //override per evitare che venga passata una specializzazione sbagliata di offerta
    @Override
    public void ottieniOfferta(Offerta offerta) {
        if (offerta instanceof OffertaRegalo) {
            super.offerte.add(offerta);
        } else {
            //illegalArgumentException per gestire piu' facilmente la coerenza del tipo di offerta
            throw new IllegalArgumentException("Si possono aggiungere solo offerte di regalo a un AnnuncioRegalo");
        }
    }
}