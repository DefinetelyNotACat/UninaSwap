-- Aggiorniamo gli oggetti di Michael Jackson con la FK all'annuncio di regalo appena creato
UPDATE OGGETTO
SET FK_IDANNUNCIOREGALO = 1
WHERE IDOGGETTO IN(1,2); -- IDOGGETTO 1: Guanto Bianco, IDOGGETTO 2: Occhiali da Sole

-- Aggiorniamo gli oggetti di Einstein con la FK all'annuncio di vendita appena creato
UPDATE OGGETTO
SET FK_IDANNUNCIOVENDITA = 1
WHERE IDOGGETTO IN(6,5); -- IDOGGETTO 6: Violino, IDOGGETTO 5: Maglione Di Lana

-- Aggiornamento degli oggetti di Gosling con la FK all'annuncio di scambio
UPDATE OGGETTO
SET FK_IDANNUNCIOSCAMBIO = 1
WHERE IDOGGETTO IN(19,21); -- IDOGGETTO 19: Felpa Con Scorpione, IDOGGETTO 21: Scheda Grafica 1080 TI

-- Aggiornamenti per gli oggetti nelle offerte di scambio
UPDATE OGGETTO
SET FK_IDOFFERTASCAMBIO = 1
WHERE IDOGGETTO IN(8,7); -- IDOGGETTO 8: Grammatica Tedesca (di Asuka), IDOGGETTO 7: Costume Plug Suit Eva-02 (di Asuka)

UPDATE OGGETTO
SET FK_IDOFFERTASCAMBIO = 2
WHERE IDOGGETTO IN(14,15);   -- IDOGGETTO 14: Abito Elegante (di Tesla), IDOGGETTO 15: Le Mie Invenzioni (di Tesla)

-- Offerte Accettate

-- Ipotizziamo che Michael Jackson regali i suoi oggetti a Ryan Gosling
UPDATE OFFERTAREGALO
SET STATOOFFERTA = 'Accettata'
WHERE IDOFFERTAREGALO = 1;

-- Ipotizziamo che Einstein venda i suoi oggetti ad Akira
UPDATE OFFERTAVENDITA
SET STATOOFFERTA = 'Accettata'
WHERE IDOffertaVendita = 1;

-- Ipotizziamo che Gosling scambi i suoi oggetti con Asuka
UPDATE OFFERTASCAMBIO
SET STATOOFFERTA = 'Accettata'
WHERE IDOffertaScambio = 1;

