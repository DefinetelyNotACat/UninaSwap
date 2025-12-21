package com.example.uninaswap.interfaces;

import com.example.uninaswap.entity.Oggetto;
import java.util.ArrayList;

public interface GestoreCondizioneDAO {
    public ArrayList<Oggetto.CONDIZIONE> ottieniTutteCondizioni();
}