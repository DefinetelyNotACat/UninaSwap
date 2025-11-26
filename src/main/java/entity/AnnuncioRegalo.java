package entity;

import java.time.LocalTime;

public class AnnuncioRegalo extends Annuncio{
    public AnnuncioRegalo(Sede sede, String descrizione, LocalTime orarioInizio, LocalTime orarioFine, Oggetto oggetto) {
        super(sede, descrizione, orarioInizio, orarioFine, oggetto);
    }
}
