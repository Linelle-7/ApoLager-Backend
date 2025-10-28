package de.Apotheke;

import data_Repo.MedikamentRepository;
import model.Ablaufsdatum;
import model.Medikament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.LagerService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LagerServiceTest {

    private LagerService service;

    @BeforeEach
    void setup() {
        //medRep = new MedikamentRepository();
        service = new LagerService();
        Medikament para=new Medikament("04324188","Paracetamol", 5,new Ablaufsdatum (2025,10,1),5);
        Medikament ibu=new Medikament("01126111","Ibuprofen", 10,new Ablaufsdatum (2025,9,15),26);
        Medikament eff=new Medikament("01126122","Efferalgan", 20, new Ablaufsdatum(2025,6,5),20);
        Medikament pen=new Medikament("02126121","Penicillin", 10,new Ablaufsdatum (2025,1,22),30);
        Medikament levo=new Medikament("04325189","Levotyroxin", 12,new Ablaufsdatum (2025,8,25),3);
        service.addMedikament(para);
        service.addMedikament(new Medikament("01111111", "paracetamol", 12,new Ablaufsdatum (2025, 8, 25), 3));
        service.addMedikament(ibu);service.addMedikament(eff);service.addMedikament(pen);service.addMedikament(levo);
    }

    @Test
    void testAddMedikament() {
        service.addMedikament(new Medikament("04888302", "Doliprane", 12,new Ablaufsdatum (2025, 8, 25), -3));
        assertThat(service.getMedikamente()) // Hier haben wir erst eine Liste von Medikamente.
                .hasSize(7)
                .first()
                .extracting(m->m.getTyp().name())
                .isEqualTo("paracetamol");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void testIncreaseCount_validValues_shouldWork(int count) {
        Ablaufsdatum heute = new Ablaufsdatum(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth()
        );
        service.increaseCount("04324188", count,heute);
        assertThat(service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188"))
                .mapToInt(Medikament::bestand)
                .sum()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void testIncreaseCount_invalidPzn_shouldThrowException() {
        Ablaufsdatum heute = new Ablaufsdatum(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth()
        );
        assertThrows(IllegalArgumentException.class,
                () -> service.increaseCount("99999999", 5, heute));

    }


    @Test
    void testIncreaseCount() {
        // existierende Medikamente aufstocken

        service.increaseCount("04324188", 10,new Ablaufsdatum(2025, 9,25)); // Paracetamol erhöhen

        //Menge erste vorkommen Med mit diesem pzn prüfen. stimmt es überein?
        assertThat(service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188"))
                .findFirst()
                .get()
                .bestand())
                .isEqualTo(10);

        //Menge letzte vorkommen Med mit diesem pzn prüfen. stimmt es überein?
        assertThat(service.getMedikamente())
                .filteredOn(m -> m.getTyp().getPzn().equals("04324188"))
                .last()
                .extracting(Medikament::bestand)
                .isEqualTo(5);

        //Menge allee Med mit diesem pzn prüfen. stimmt es überein?
       assertThat(service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188")) // filtert alle Medikamente mit dieser PZN
                .mapToInt(Medikament::bestand)            // holt die Bestände
                .sum())
                .isEqualTo(15);

       //nicht existierende Medikamente aufstocken
        try{
            service.increaseCount("04324100", 10,new Ablaufsdatum(2025, 9,25));
        }catch (Exception e){
            System.out.println(e);
        }

    }

    @Test
    void testSell() {
        // Existierenden Medikamente verkaufen

        assertThat(service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188"))
                .findFirst()
                .get()
                .bestand())
                .isEqualTo(5);


        try{
            service.sell("04324188", 10);
        } catch (Exception e) {
            System.out.println(e);
        }
//        assertThat(service.getMedikamente().stream()
//                .filter(m -> m.getTyp().getPzn().equals("04324188"))
//                .findFirst()
//                .get()
//                .bestand())
//                .isEqualTo(5);


        try{
            service.sell("01126111", 10);
        } catch (Exception e) {
            System.out.println(e);
        }

        assertThat(service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("01126111"))
                .findFirst()
                .get()
                .bestand())
                .isGreaterThan(5);

        Medikament med=service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("01126111"))
                .findFirst()
                .orElseThrow(()-> new AssertionError(" Kein Medikament mit gegebene PZN gefunden"));


        // Nicht existierendes Medikament verkaufen
        try{
            service.sell("04324199", 10);
        }catch (Exception e1){
            System.out.println(e1);

        }
        //service.sell("04324188", 10);

    }

    @Test
    void testStatistik() {
        // ????
    }

    @Test
    void testInventur() {
        // ????
        service.inventur();
    }

    @Test
    void testToString() {
        assertThat(service.printLager()).contains("paracetamol");
    }
}
