
package com.example.uninaswap.boundary;

public class Messaggio {
    String descrizione;
    boolean operazioneRiuscita;
    public Messaggio(boolean operazioneRiuscita,String descrizione) {
        this.operazioneRiuscita = operazioneRiuscita;
        this.descrizione = descrizione;
        mostraMessaggio(operazioneRiuscita,descrizione);
    }
    public static void mostraMessaggio(boolean operazioneRiuscita, String descrizione) {
        if(operazioneRiuscita){

        }
        else{

        }

    }
}
