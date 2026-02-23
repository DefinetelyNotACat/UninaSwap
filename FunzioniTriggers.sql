
-- 1) Trigger: Un solo impiego per OGGETTO
CREATE OR REPLACE FUNCTION VERIFICA_UNICO_IMPIEGO()
RETURNS TRIGGER AS $$
DECLARE
    NUM_IMPIEGHI INTEGER;
BEGIN
    NUM_IMPIEGHI :=
        (CASE WHEN NEW.FK_idOffertaScambio IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN NEW.FK_idAnnuncioRegalo IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN NEW.FK_idAnnuncioVendita IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN NEW.FK_idAnnuncioScambio IS NOT NULL THEN 1 ELSE 0 END);

    IF NUM_IMPIEGHI > 1 THEN
        RAISE EXCEPTION 'ERRORE: UN OGGETTO PUÒ AVERE AL MASSIMO UN SOLO IMPIEGO TRA OFFERTA SCAMBIO, ANNUNCIO REGALO, ANNUNCIO VENDITA O ANNUNCIO SCAMBIO.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER OGGETTO_UN_SOLO_IMPIEGO
BEFORE INSERT OR UPDATE ON OGGETTO
FOR EACH ROW
EXECUTE FUNCTION VERIFICA_UNICO_IMPIEGO();

-- 2) Trigger: Uppercase MATRICOLA
CREATE OR REPLACE FUNCTION converti_matricola_uppercase()
RETURNS TRIGGER AS $$
BEGIN
    NEW.MATRICOLA := UPPER(NEW.MATRICOLA);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER rendi_matricola_uppercase
BEFORE INSERT OR UPDATE ON UTENTE
FOR EACH ROW
EXECUTE FUNCTION converti_matricola_uppercase();

-- 3) Trigger: Verifica prezzo offerta
CREATE OR REPLACE FUNCTION verifica_prezzo_offerta()
RETURNS TRIGGER AS $$
DECLARE
    prezzo_minimo_annuncio NUMERIC(10,2);
BEGIN
    SELECT PREZZOMINIMO
    INTO prezzo_minimo_annuncio
    FROM ANNUNCIOVENDITA
    WHERE idAnnuncioVendita = NEW.FK_idAnnuncioVendita;

    IF prezzo_minimo_annuncio IS NOT NULL AND NEW.PREZZO < prezzo_minimo_annuncio THEN
        RAISE EXCEPTION 
            'ERRORE: il prezzo della tua offerta (%.2f) non deve essere inferiore al prezzo minimo (%.2f).',
            NEW.PREZZO, prezzo_minimo_annuncio;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER isValidPrezzoOfferta
BEFORE INSERT OR UPDATE ON OFFERTAVENDITA
FOR EACH ROW
EXECUTE FUNCTION verifica_prezzo_offerta();

-- 4) Trigger: Disponibilità oggetto in base a impiego
CREATE OR REPLACE FUNCTION forza_occupato_se_impiegato()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.FK_idOffertaScambio IS NULL AND
       OLD.FK_idAnnuncioRegalo IS NULL AND
       OLD.FK_idAnnuncioVendita IS NULL AND
       OLD.FK_idAnnuncioScambio IS NULL AND
       (
           NEW.FK_idOffertaScambio IS NOT NULL OR
           NEW.FK_idAnnuncioRegalo IS NOT NULL OR
           NEW.FK_idAnnuncioVendita IS NOT NULL OR
           NEW.FK_idAnnuncioScambio IS NOT NULL
       )
    THEN
        NEW.disponibilita := 'Occupato';

    ELSIF NEW.FK_idOffertaScambio IS NULL AND
          NEW.FK_idAnnuncioRegalo IS NULL AND
          NEW.FK_idAnnuncioVendita IS NULL AND
          NEW.FK_idAnnuncioScambio IS NULL
    THEN
        NEW.disponibilita := 'Disponibile';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER oggetto_occupato
BEFORE UPDATE ON oggetto
FOR EACH ROW
EXECUTE FUNCTION forza_occupato_se_impiegato();

-- 5) Trigger: Logica vendita
CREATE OR REPLACE FUNCTION aggiorna_vendita()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ANNUNCIOVENDITA
    SET STATOANNUNCIO = 'Non Disponibile'
    WHERE idAnnuncioVendita = NEW.FK_idAnnuncioVendita;

    UPDATE OGGETTO
    SET DISPONIBILITA = 'Venduto'
    WHERE FK_idAnnuncioVendita = NEW.FK_idAnnuncioVendita;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER LOGICA_OFFERTAVENDITA_ANNUNCIO_OGGETTO
AFTER UPDATE OF STATOOFFERTA ON OFFERTAVENDITA
FOR EACH ROW
WHEN (NEW.STATOOFFERTA = 'Accettata')
EXECUTE FUNCTION aggiorna_vendita();

-- 6) Trigger: Logica scambio
CREATE OR REPLACE FUNCTION aggiorna_scambio()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE OGGETTO
    SET DISPONIBILITA = 'Scambiato'
    WHERE FK_idOffertaScambio = NEW.idOffertaScambio;

    DELETE FROM OFFERTASCAMBIO
    WHERE FK_idAnnuncioScambio = NEW.FK_idAnnuncioScambio
      AND idOffertaScambio <> NEW.idOffertaScambio;

    UPDATE ANNUNCIOSCAMBIO
    SET STATOANNUNCIO = 'Non Disponibile'
    WHERE idAnnuncioScambio = NEW.FK_idAnnuncioScambio;

    UPDATE OGGETTO
    SET DISPONIBILITA = 'Scambiato'
    WHERE FK_idAnnuncioScambio = NEW.FK_idAnnuncioScambio
      AND DISPONIBILITA <> 'Scambiato';

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER LOGICA_OFFERTASCAMBIO_ANNUNCIO_OGGETTO
AFTER UPDATE OF STATOOFFERTA ON OFFERTASCAMBIO
FOR EACH ROW
WHEN (NEW.STATOOFFERTA = 'Accettata')
EXECUTE FUNCTION aggiorna_scambio();

-- 7) Trigger: Logica regalo
CREATE OR REPLACE FUNCTION aggiorna_regalo()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ANNUNCIOREGALO
    SET STATOANNUNCIO = 'Non Disponibile'
    WHERE idAnnuncioRegalo = NEW.FK_idAnnuncioRegalo;

    UPDATE OGGETTO
    SET DISPONIBILITA = 'Regalato'
    WHERE FK_idAnnuncioRegalo = NEW.FK_idAnnuncioRegalo;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER LOGICA_OFFERTAREGALO_ANNUNCIOREGALO_OGGETTO
AFTER UPDATE OF STATOOFFERTA ON OFFERTAREGALO
FOR EACH ROW
WHEN (NEW.STATOOFFERTA = 'Accettata')
EXECUTE FUNCTION aggiorna_regalo();

-- 8) Trigger: Hash password
CREATE OR REPLACE FUNCTION check_and_hash_password()
RETURNS TRIGGER AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    IF LENGTH(NEW.PASSWORD) < 8 THEN
        RAISE EXCEPTION 'La password deve essere lunga almeno 8 caratteri';
    END IF;

    IF NEW.PASSWORD ~ '^\s' OR NEW.PASSWORD ~ '\s$' THEN
        RAISE EXCEPTION 'La password non può iniziare o finire con uno spazio';
    END IF;

    IF NEW.PASSWORD !~ '[0-9]' THEN
        RAISE EXCEPTION 'La password deve contenere almeno un numero';
    END IF;

    IF NEW.PASSWORD !~ '[^A-Za-z0-9]' THEN
        RAISE EXCEPTION 'La password deve contenere almeno un simbolo';
    END IF;

    NEW.PASSWORD := encode(digest(NEW.PASSWORD, 'sha256'), 'hex');

  ELSIF TG_OP = 'UPDATE' THEN
    IF NEW.PASSWORD IS DISTINCT FROM OLD.PASSWORD THEN
        IF LENGTH(NEW.PASSWORD) < 8 THEN
            RAISE EXCEPTION 'La password deve essere lunga almeno 8 caratteri';
        END IF;

        IF NEW.PASSWORD ~ '^\s' OR NEW.PASSWORD ~ '\s$' THEN
            RAISE EXCEPTION 'La password non può iniziare o finire con uno spazio';
        END IF;

        IF NEW.PASSWORD !~ '[0-9]' THEN
            RAISE EXCEPTION 'La password deve contenere almeno un numero';
        END IF;

        IF NEW.PASSWORD !~ '[^A-Za-z0-9]' THEN
            RAISE EXCEPTION 'La password deve contenere almeno un simbolo';
        END IF;

        NEW.PASSWORD := encode(digest(NEW.PASSWORD, 'sha256'), 'hex');
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER HashPassword
BEFORE INSERT OR UPDATE ON UTENTE
FOR EACH ROW
EXECUTE FUNCTION check_and_hash_password();

-- 9) Orario coerente OFFERTASCAMBIO
CREATE OR REPLACE FUNCTION check_orario_offerta_scambio()
RETURNS TRIGGER AS $$
DECLARE
  orario_inizio_annuncio TIME;
  orario_fine_annuncio TIME;
BEGIN
  SELECT ORARIOINIZIO, ORARIOFINE
  INTO orario_inizio_annuncio, orario_fine_annuncio
  FROM ANNUNCIOSCAMBIO
  WHERE idAnnuncioScambio = NEW.FK_idAnnuncioScambio;

  IF NEW.ORARIOINIZIO < orario_inizio_annuncio OR NEW.ORARIOFINE > orario_fine_annuncio THEN
    RAISE EXCEPTION 'il range della tua offerta di scambio deve essere inclusa nel range [% - %] del suo annuncio.', orario_inizio_annuncio, orario_fine_annuncio;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER OrarioOffertaScambioAnnuncioScambioCoerenti
BEFORE INSERT OR UPDATE ON OFFERTASCAMBIO
FOR EACH ROW
EXECUTE FUNCTION check_orario_offerta_scambio();

-- 10) Orario coerente OFFERTAREGALO
CREATE OR REPLACE FUNCTION check_orario_offerta_regalo()
RETURNS TRIGGER AS $$
DECLARE
  orario_inizio_annuncio TIME;
  orario_fine_annuncio TIME;
BEGIN
  SELECT ORARIOINIZIO, ORARIOFINE
  INTO orario_inizio_annuncio, orario_fine_annuncio
  FROM ANNUNCIOREGALO
  WHERE idAnnuncioRegalo = NEW.FK_idAnnuncioRegalo;

  IF NEW.ORARIOINIZIO < orario_inizio_annuncio OR NEW.ORARIOFINE > orario_fine_annuncio THEN
    RAISE EXCEPTION 'Il range della tua offerta di regalo deve essere incluso nel range [% - %] del suo annuncio.', orario_inizio_annuncio, orario_fine_annuncio;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER OrarioOffertaRegaloAnnuncioRegaloCoerenti
BEFORE INSERT OR UPDATE ON OFFERTAREGALO
FOR EACH ROW
EXECUTE FUNCTION check_orario_offerta_regalo();

-- 11) Orario coerente OFFERTAVENDITA
CREATE OR REPLACE FUNCTION check_orario_offerta_vendita()
RETURNS TRIGGER AS $$
DECLARE
  orario_inizio_annuncio TIME;
  orario_fine_annuncio TIME;
BEGIN
  SELECT ORARIOINIZIO, ORARIOFINE
  INTO orario_inizio_annuncio, orario_fine_annuncio
  FROM ANNUNCIOVENDITA
  WHERE idAnnuncioVendita = NEW.FK_idAnnuncioVendita;

  IF NEW.ORARIOINIZIO < orario_inizio_annuncio OR NEW.ORARIOFINE > orario_fine_annuncio THEN
    RAISE EXCEPTION 'Il range della tua offerta di vendita deve essere incluso nel range [% - %] del suo annuncio.', orario_inizio_annuncio, orario_fine_annuncio;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER OrarioOffertaVenditaAnnuncioVenditaCoerenti
BEFORE INSERT OR UPDATE ON OFFERTAVENDITA
FOR EACH ROW
EXECUTE FUNCTION check_orario_offerta_vendita();



-- 12) Verifica proprietario offerta regalo
CREATE OR REPLACE FUNCTION verifica_proprietario_offerta_regalo()
RETURNS TRIGGER AS $$
DECLARE
    annuncio_owner_matricola CHAR(9);
BEGIN
    SELECT U.MATRICOLA
    INTO annuncio_owner_matricola
    FROM UTENTE U
    JOIN OGGETTO O ON U.MATRICOLA = O.FK_idUtente
    WHERE O.FK_idAnnuncioRegalo = NEW.FK_idAnnuncioRegalo;

    IF NEW.FK_idUtente = annuncio_owner_matricola THEN
        RAISE EXCEPTION 'Il proprietario di un annuncio di regalo non può fare un''offerta sul proprio annuncio.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER CheckProprietarioOfferenteRegalo
BEFORE INSERT ON OFFERTAREGALO
FOR EACH ROW
EXECUTE FUNCTION verifica_proprietario_offerta_regalo();


-- 13) Verifica proprietario offerta vendita
CREATE OR REPLACE FUNCTION verifica_proprietario_offerta_vendita()
RETURNS TRIGGER AS $$
DECLARE
    annuncio_owner_matricola CHAR(9);
BEGIN
    SELECT U.MATRICOLA
    INTO annuncio_owner_matricola
    FROM UTENTE U
    JOIN OGGETTO O ON U.MATRICOLA = O.FK_idUtente
    WHERE O.FK_idAnnuncioVendita = NEW.FK_idAnnuncioVendita;

    IF NEW.FK_idUtente = annuncio_owner_matricola THEN
        RAISE EXCEPTION 'Il proprietario di un annuncio di vendita non può fare un''offerta sul proprio annuncio.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER CheckProprietarioOfferenteVendita
BEFORE INSERT ON OFFERTAVENDITA
FOR EACH ROW
EXECUTE FUNCTION verifica_proprietario_offerta_vendita();


-- 14) Verifica proprietario offerta scambio
CREATE OR REPLACE FUNCTION verifica_proprietario_offerta_scambio()
RETURNS TRIGGER AS $$
DECLARE
    annuncio_owner_matricola CHAR(9);
BEGIN
    SELECT U.MATRICOLA
    INTO annuncio_owner_matricola
    FROM UTENTE U
    JOIN OGGETTO O ON U.MATRICOLA = O.FK_idUtente
    WHERE O.FK_idAnnuncioScambio = NEW.FK_idAnnuncioScambio;

    IF NEW.FK_idUtente = annuncio_owner_matricola THEN
        RAISE EXCEPTION 'Il proprietario di un annuncio di scambio non può fare un''offerta sul proprio annuncio.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER CheckProprietarioOfferenteScambio
BEFORE INSERT ON OFFERTASCAMBIO
FOR EACH ROW
EXECUTE FUNCTION verifica_proprietario_offerta_scambio();