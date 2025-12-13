package com.example.uninaswap.interfaces;
import com.example.uninaswap.boundary.Messaggio;

public interface GestoreMessaggio {
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo);
}