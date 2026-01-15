package com.example.uninaswap.entity;
import com.example.uninaswap.Costanti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import static com.example.uninaswap.Costanti.pathUtenti;
import static com.example.uninaswap.Costanti.pathImmagineDiProfiloDefault;

public class Utente {

    //Attributi
    //
    private int id;
    private String username;
    private String password;
    private String matricola;
    private String email;
    private String pathImmagineProfilo;
    private ArrayList<Offerta> offerte = new ArrayList<Offerta>();
    private ArrayList<Oggetto> oggetti = new ArrayList<Oggetto>();
    private ArrayList<Recensione> recensioniInviata = new ArrayList<>();
    private ArrayList<Recensione> recensioniRicevuta = new ArrayList<>();

    //Costruttori
    //

    public Utente(){}

    public Utente(String username, String password, String matricola, String email) {
        this.username = username;
        this.password = password;
        this.matricola = matricola;
        this.email = email;
        this.pathImmagineProfilo = pathImmagineDiProfiloDefault;
    }

    //Metodi di logica
    //
    public String modificaImmagineProfilo(String pathImmagineCaricata) throws IOException {
        System.out.println("Sono nella modifica e l'id e': " + this.id);
        //Genera il path di destinazione dell'immagine: dati_utenti/{id}/immagini
        //
        Path cartellaUtente = Paths.get(pathUtenti, String.valueOf(this.id), "immagini");

        System.out.println("La cartella per le immagini e': " + Paths.get(cartellaUtente.toString()));

        //Se le cartelle non esistono le crea tutte in una sola volta
        //
        if (!Files.exists(cartellaUtente)) {
            System.out.println("La cartella per le immagini non esiste, la creo");

            Files.createDirectories(cartellaUtente);
        }

        //Fa diventare la stringa passata un oggetto di tipo Path
        //
        Path sorgenteImmagineCaricata = Paths.get(pathImmagineCaricata);

        //Faccio diventare la stringa contenente il path del file che l'utente sta caricando in un oggetto di tipo Path (Paths.get())
        //in maniera tale da ricavarci solo l'ultima parte che diventera' a sua volta un oggetto Path (.getFileName()) e poi lo passiamo come stringa attraverso il .toString()
        //
        String immagineCaricata = Paths.get(pathImmagineCaricata).getFileName().toString();

        //Ci ricaviamo l'estensione dal file appena caricato
        //
        int indiceEstensione = immagineCaricata.lastIndexOf('.');
        String estensioneFile = immagineCaricata.substring(indiceEstensione);

        //Diamo un nome al file che andremo a salvare
        //
        String nomeFileFinale = "immagine_profilo" + estensioneFile;

        //Definizione della del path che l'immagine avra' dopo il salvataggio attraverso cartellaUtente.resolve(nomeFileFile), resolve unisce il path della cartella con il nome del file
        //
        Path pathDestinazione = cartellaUtente.resolve(nomeFileFinale);


        System.out.println("Questo e' il path dell'immagine originale : " + sorgenteImmagineCaricata.toString());

        //Copia del file caricato nella cartella di destinazione con il nuovo nome, in caso il file esista gia' lo sovrascrive
        //
        Files.copy(sorgenteImmagineCaricata, pathDestinazione,  StandardCopyOption.REPLACE_EXISTING);

        //Ritorna come stringa il path relativo dell'immagine
        //
        return pathImmagineProfilo = Paths.get(String.valueOf(id), Costanti.NOME_CARTELLA_IMMAGINI_PFP, nomeFileFinale).toString();

    }

    //Adder, Remover e Clearer
    //
    public boolean addRecensioneInviata(Recensione recensione) {
        return this.recensioniInviata.add(recensione);
    }

    public boolean removeRecensioneInviata(Recensione recensione) {
        return this.recensioniInviata.remove(recensione);
    }

    public void clearRecensioneInviata() {
        this.recensioniInviata.clear();
    }

    public boolean addRecensioneRicevuta(Recensione recensione) {
        return this.recensioniRicevuta.add(recensione);
    }

    public boolean removeRecensioneRicevuta(Recensione recensione) {
        return this.recensioniRicevuta.remove(recensione);
    }

    public void clearRecensioneRicevuta() {
        this.recensioniRicevuta.clear();
    }

    public boolean addOggetto(Oggetto oggetto){
        return this.oggetti.add(oggetto);
    }

    public boolean removeOggetto(Oggetto oggetto){
        return this.oggetti.remove(oggetto);
    }

    public void clearOggetto(){
        this.oggetti.clear();
    }

    public void clearOfferte(){
        this.offerte.clear();
    }

    //Setter e Getter
    //
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOfferta(Offerta offerta) {
        this.offerte.add(offerta);
    }

    public void setPathImmagineProfilo(String pathImmagineProfilo) {
        this.pathImmagineProfilo = pathImmagineProfilo;
    }

    public String getPathImmagineProfilo() {
        return pathImmagineProfilo;
    }

    public ArrayList<Offerta> getOfferte() {
        return offerte;
    }

    public ArrayList<Oggetto> getOggetti() {
        return oggetti;
    }

    public ArrayList<Recensione> getRecensioniInviate() {
        return recensioniInviata;
    }

    public ArrayList<Recensione> getRecensioniRicevute() {
        return recensioniRicevuta;
    }

    public void setOggetti(ArrayList<Oggetto> oggetti) {
        oggetti = oggetti;
    }

    //toString
    //
    @Override
    public String toString() {
        return "Username : " + this.username + " Email : " + this.email + " Matricola : " + this.matricola + " Password : " + this.password;
    }

}