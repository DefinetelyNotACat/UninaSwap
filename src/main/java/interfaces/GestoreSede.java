package interfaces;

import entity.Sede;

import java.util.ArrayList;

public interface GestoreSede {
    public boolean salvaSede(Sede sede);
    public boolean modificaSede(Sede sede);
    public boolean salvaSede(int id);
    public boolean OttieniSede(int id);
    public ArrayList<Sede> OttieniSedi();
}
