package entity;

import java.time.LocalTime;

public class AnnuncioScambio extends Annuncio{
    public AnnuncioScambio(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }
}
