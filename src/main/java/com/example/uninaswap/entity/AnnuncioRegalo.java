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

    //override di getTipoAnnuncio per specificare che è un AnnuncioRegalo
    @Override
    public String getTipoAnnuncio(){
        return "Regalo";
    }

    //override per evitare che venga passata una specializzazione sbagliata di offerta
    @Override
    public void ottieniOfferta(Offerta offerta) throws Exception {
        if (offerta instanceof OffertaRegalo) {
            super.offerte.add(offerta);
        } else {
            throw new Exception("Offerta di tipo sbagliato");
        }
    }
}