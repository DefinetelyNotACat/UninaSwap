# UninaSwap

UninaSwap e' il progetto che abbiamo sviluppato per il corso di Programmazione ad Oggetti.
Lo scopo dell'app e' quella di fornire la possibilita' agli studenti della Federico II di scambiarsi, vendersi o regalarsi oggetti tra di loro in modo piu' facile

## Cosa fa l'app
In breve, l'applicazione permette di:
* Registrarsi e creare un profilo attraverso la mail istituzionale.
* Pubblicare annunci (Vendo, Scambio, Regalo).
* Cercare Annunci di oggetti a cui potrebbero essere interessati.
* Fare offerte e gestire le trattative.
* Lasciare recensioni agli altri utenti.

## Setup dell'Applicazione

Per far funzionare l'applicazione e' necessario avere installato la JDK dalla versione 17 in poi e un'istanza PostgreSQL.
Per facilitarne l'uso abbiamo realizzato uno script che elimina il database se lo si aveva gia' e lo ricrea in automatico.

### Come importare il DB con pgAdmin:

1. Apri **pgAdmin** e crea un nuovo database vuoto (chiamalo ad esempio `uninaswap`).
2. Tasto destro sul database appena creato -> **Query Tool**.
3. Apri (o copia-incolla) il file `elimina_crea_db.sql` che trovi nella cartella principale di questa repository.
4. Esegui lo script.

## Configurazione Java

Prima di avviare il `Main`, bisogna creare il proprio file **.env** con i dati del proprio DB.
Qui un esempio di come dovrebbe essere strutturato il file **.env**.
```java
static final String USER = "postgres";
static final String PASS = "la_tua_password";
static final String DB_URL = "jdbc:postgresql://localhost:5432/uninaswap";
```
