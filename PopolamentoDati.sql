-- ===========================
-- POPOLAMENTO TABELLE UTENTE, OGGETTO, SEDE
-- ===========================

-- Inserimento UTENTI
INSERT INTO UTENTE (MATRICOLA, USERNAME, EMAIL, PASSWORD) VALUES
    ('N86005982', 'rick.sanchez',     'rick@gmail.com',        'Pickle!123'),
    ('N86005874', 'asuka.langley',    'asuka@gmail.com',       'ShinjiBaka@22'),
    ('N86005917', 'akira.neo',        'akira@gmail.com',       'NeoTokyo@88'),
    ('N86005763', 'albert_einstein',  'einstein@gmail.com',    'E=mc2!2025'),
    ('N86005801', 'nikola.tesla',     'tesla@gmail.com',       'Tesla369$'),
    ('N86005899', 'michael.jackson',  'mj@gmail.com',          'HeeHee#1982'),
    ('N86006001', 'john.von_neumann', 'vonneumann@gmail.com',  'BinaryGenius42!'),
    ('N86006902', 'ryan.gosling',     'gosling@gmail.com',     'KDrive2049!');

-- Inserimento OGGETTI
INSERT INTO OGGETTO (NOME, CONDIZIONE, CATEGORIA, DISPONIBILITA, FK_idUtente, FK_idOffertaScambio, FK_idAnnuncioRegalo, FK_idAnnuncioVendita, FK_idAnnuncioScambio) VALUES
-- Michael Jackson
('Guanto Bianco', 'Ottime Condizioni', 'Abbigliamento', 'Disponibile', 'N86005899', NULL, NULL, NULL, NULL),
('Occhiali da Sole', 'Buone Condizioni', 'Abbigliamento', 'Disponibile', 'N86005899', NULL, NULL, NULL, NULL),
('Microfono', 'Buone Condizioni', 'Strumenti Musicali', 'Disponibile', 'N86005899', NULL, NULL, NULL, NULL),

-- Albert Einstein
('Appunti Fisica Relativa', 'Come Nuovo', 'Libri di testo', 'Disponibile', 'N86005763', NULL, NULL, NULL, NULL),
('Maglione Di Lana', 'Ottime Condizioni', 'Abbigliamento', 'Disponibile', 'N86005763', NULL, NULL, NULL, NULL),
('Violino', 'Discrete Condizioni', 'Strumenti Musicali', 'Disponibile', 'N86005763', NULL, NULL, NULL, NULL),

-- Asuka
('Costume Plug Suit Eva-02', 'Come Nuovo', 'Abbigliamento', 'Disponibile', 'N86005874', NULL, NULL, NULL, NULL),
('Grammatica Tedesca', 'Ottime Condizioni', 'Libri di testo', 'Disponibile', 'N86005874', NULL, NULL, NULL, NULL),
('Molletta Per Capelli', 'Buone Condizioni', 'Abbigliamento', 'Disponibile', 'N86005874', NULL, NULL, NULL, NULL),

-- Rick Sanchez
('Portale Multiversale Portatile', 'Ottime Condizioni', 'Materiale informatico', 'Disponibile', 'N86005982', NULL, NULL, NULL, NULL),
('Camice Bianco Da Scienziato', 'Buone Condizioni', 'Abbigliamento', 'Disponibile', 'N86005982', NULL, NULL, NULL, NULL),
('Cavo HDMI interdimensionale', 'Buone Condizioni', 'Materiale informatico', 'Disponibile', 'N86005982', NULL, NULL, NULL, NULL),

-- Nikola Tesla
('Bobina di Tesla Originale', 'Ottime Condizioni', 'Materiale informatico', 'Disponibile', 'N86005801', NULL, NULL, NULL, NULL),
('Abito Elegante', 'Buone Condizioni', 'Abbigliamento', 'Disponibile', 'N86005801', NULL, NULL, NULL, NULL),
('Le Mie Invenzioni', 'Come Nuovo', 'Libri di testo', 'Disponibile', 'N86005801', NULL, NULL, NULL, NULL),

-- Von Neumann
('Appunti Game Theory', 'Nuovo', 'Libri di testo', 'Disponibile', 'N86006001', NULL, NULL, NULL, NULL),
('Schede perforate IBM', 'Discrete Condizioni', 'Materiale informatico', 'Disponibile', 'N86006001', NULL, NULL, NULL, NULL),
('Kit Macchina di Turing (Replica)', 'Nuovo', 'Materiale informatico', 'Disponibile', 'N86006001', NULL, NULL, NULL, NULL),

-- Ryan Gosling
('Felpa Con Scorpione', 'Cattive Condizioni', 'Abbigliamento','Disponibile', 'N86006902', NULL, NULL, NULL, NULL),
('Ologramma Portatile', 'Buone Condizioni', 'Materiale informatico','Disponibile', 'N86006902', NULL, NULL, NULL, NULL),
('Scheda Grafica 1080 TI', 'Ottime Condizioni', 'Materiale informatico', 'Disponibile', 'N86006902', NULL, NULL, NULL, NULL);

-- Inserimento SEDI
INSERT INTO SEDE (NOMESEDE, VIA, CAP, CITTA) VALUES
('Complesso Universitario di Monte Sant angelo', 'Via Cinthia 26', '80126', 'Napoli'),
('Biennio Ingegneria', 'Via Claudio 21', '80125', 'Napoli'),
('Triennio Ingegneria', 'Via Claudio 21', '80125', 'Napoli'),
('Via Partenope', 'Via Partenope 36', '80121', 'Napoli'),
('Don Bosco', 'Via Don Bosco 8', '80141', 'Napoli'),
('Reggia di Portici', 'Via Universita 100', '80055', 'Portici'),
('Palazzo Mascabruno', 'Via Universita 133', '80055', 'Portici'),
('Delpino', 'Via Delpino 1', '80138', 'Napoli'),
('Orto Botanico', 'Via Foria 223', '80137', 'Napoli'),
('Museo di Anatomia Veterinaria', 'Via Federico Delpino 1', '80137', 'Napoli'),
('Montesano', 'Via Montesano 49', '80135', 'Napoli'),
('Via De Amicis', 'Via Edmondo De Amicis 95', '80128', 'Napoli'),
('Azienda Ospedaliera Universitaria', 'Via Pansini 5', '80131', 'Napoli'),
('Policlinico', 'Via Pansini 5', '80131', 'Napoli'),
('Sede Centrale', 'Corso Umberto I 40', '80138', 'Napoli'),
('Palazzo degli Uffici', 'Via Giulio Cesare Cortese 29', '80133', 'Napoli'),
('Via Nuova Marina', 'Via Nuova Marina 33', '80133', 'Napoli'),
('Porta di Massa 32', 'Via Porta di Massa 32', '80133', 'Napoli'),
('Via Mezzocannone', 'Via Mezzocannone 16', '80134', 'Napoli'),
('Via Rodino', 'Via G Rodino 22', '80138', 'Napoli'),
('Via Porta di Massa', 'Via Porta di Massa 1', '80133', 'Napoli'),
('San Marcellino', 'Largo San Marcellino 10', '80138', 'Napoli'),
('Via Mezzocannone 2', 'Via Mezzocannone 2', '80134', 'Napoli'),
('Via Mezzocannone 8', 'Via Mezzocannone 8', '80134', 'Napoli'),
('Complesso Sant Antoniello', 'Vico S Aniello a Caponapoli 10', '80138', 'Napoli'),
('Palazzo Gravina', 'Via Monteoliveto 3', '80134', 'Napoli'),
('Complesso dello Spirito Santo', 'Via Toledo 402', '80134', 'Napoli'),
('Palazzo Latilla', 'Via Tarsia 31', '80135', 'Napoli'),
('Vico Monte di Pieta', 'Vico Monte di Pieta 1', '80134', 'Napoli'),
('Complesso di San Giovanni a Teduccio', 'Corso Nicolangelo Protopisani 70', '80146', 'Napoli');


-- Michael Jackson pubblica un annuncio regalo
INSERT INTO ANNUNCIOREGALO (DESCRIZIONE, ORARIOINIZIO, ORARIOFINE, FK_IDSEDE) VALUES
('Ciao a tutti! regalo il mio guanto bianco e i miei occhiali da sole', '10:00', '10:30', 1);

-- Ryan  Gosling e Rick Sanchez vogliono questo regalo e quindi creano un'offertaregalo
INSERT INTO OFFERTAREGALO (MESSAGGIO, FK_IDUTENTE, FK_IDANNUNCIOREGALO, ORARIOINIZIO, ORARIOFINE) VALUES
('Ciao Michael! sono un tuo grande fan, e vorrei avere la fortuna di possedere i tuoi regali', 'N86006902', 1, '10:00', '10:15'),
(NULL, 'N86005982', 1, '10:10', '10:15');

-- Albert Einstein vende il suo maglione e violino per 120 euro, minimo 100 e quindi crea un annunciovendita
INSERT INTO ANNUNCIOVENDITA (DESCRIZIONE, ORARIOINIZIO, ORARIOFINE, PREZZO, PREZZOMINIMO, FK_IDSEDE) VALUES
('Salve, vendo il mio maglione di lana cucito con tanto amore e il mio violino', '10:40', '12:00', 120.00, 100.00, 30);

-- Akira e Asuka vogliono comprare gli oggetti di Einstein e quindi creano un'offertavendita
INSERT INTO OFFERTAVENDITA (MESSAGGIO, PREZZO, FK_IDUTENTE, FK_IDANNUNCIOVENDITA, ORARIOINIZIO, ORARIOFINE) VALUES
('Hey Einstein, sono interessato, ecco la mia offerta', 110.00, 'N86005917', 1, '10:50', '11:30'),
('Ciao Albert, questa è la mia offerta', 100.00, 'N86005874', 1, '10:50', '11:30');

-- Ryan Gosling vuole scambiare la sua felpa con scorpione e scheda grafica per un costume e un libro
-- quindi crea un annuncioscambio
INSERT INTO ANNUNCIOSCAMBIO (DESCRIZIONE, ORARIOINIZIO, ORARIOFINE, NOMIITEMS, FK_IDSEDE) VALUES
('Ciao a tutti, vorrei darvi la mia felpa con scorpione e scheda grafica 1080 TI', '13:50', '15:20', 'Mi servirebbero un costume e un libro a vostra scelta', 29);

-- Asuka e Rick Sanchez vogliono barattare con Ryan Gosling e quindi creano un'offertascambio

INSERT INTO OFFERTASCAMBIO (MESSAGGIO, FK_IDUTENTE, FK_IDANNUNCIOSCAMBIO, ORARIOINIZIO, ORARIOFINE) VALUES
    ('Ciao Ryan, sono disposta a darti il mio costume plug suit eva 02 e il mio libro di grammatica tedesca', 'N86005874', 1, '14:50', '15:20'),
    ('Salve signor Gosling, posso offrirle la mia autobiografia e il mio abito elegante', 'N86005801', 1, '15:00', '15:15');

INSERT INTO RECENSIONE (FK_idUtenteRecensito, FK_idUtenteRecensore, VOTO, COMMENTO) VALUES
('N86005899', 'N86006902', 5, 'Michael Jackson è stato gentilissimo, il guanto e gli occhiali erano perfetti!'), -- Ryan Gosling recensisce Michael Jackson
('N86005763', 'N86005917', 4, 'Einstein ha venduto gli oggetti come descritto, transazione veloce.'), -- Akira recensisce Albert Einstein
('N86006902', 'N86005874', 5, 'Ryan Gosling è stato fantastico, felpa e schede come dalla foto!'); -- Asuka recensisce Ryan Gosling
