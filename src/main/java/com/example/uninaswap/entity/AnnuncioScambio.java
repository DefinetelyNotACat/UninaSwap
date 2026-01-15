package com.example.uninaswap.entity;

import java.time.LocalTime;
import java.util.ArrayList;
public class AnnuncioScambio extends Annuncio{
    private String listaOggetti;

    public AnnuncioScambio() {
        super();
    }

    public AnnuncioScambio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto,
                           String listaOggetti) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
        this.listaOggetti = listaOggetti;
    }
    public String getListaOggetti() {
        return listaOggetti;
    }
    public void setListaOggetti(String listaOggetti) {
        this.listaOggetti = listaOggetti;
    }

    //override del metodo della classe Annuncio per specificare che è un AnnuncioScambio
    @Override
    public String getTipoAnnuncio(){
        return "Scambio";
    }

    //override per evitare che venga passata una specializzazione sbagliata di offerta
    @Override
    public void ottieniOfferta(Offerta offerta) throws Exception {
        if(offerta instanceof OffertaScambio) {
            super.offerte.add(offerta);
        }
        else{
            throw new Exception("Offerta di tipo sbagliato");
        }
    }

}
