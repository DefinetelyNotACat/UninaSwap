package entity;

import java.time.LocalTime;

public class AnnuncioVendita extends Annuncio{
    public AnnuncioVendita(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }
}
