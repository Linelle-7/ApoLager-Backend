package de.Apotheke;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import service.LagerService;
import model.Ablaufdatum;
import model.Medikament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LagerServiceTest2 {

    private LagerService service;
    @BeforeEach
    void setup() {
        //medRep = new MedikamentRepository();
        service = new LagerService();
        Medikament para=new Medikament("04324188","Paracetamol", 5,new Ablaufdatum(2025,10,25),5);
        Medikament ibu=new Medikament("01126111","Ibuprofen", 10,new Ablaufdatum(2026,9,15),26);
        Medikament eff=new Medikament("01126122","Efferalgan", 20, new Ablaufdatum(2025,12,5),20);
        Medikament pen=new Medikament("02126121","Penicillin", 10,new Ablaufdatum(2025,10,20),30);
        Medikament levo=new Medikament("04325189","Levotyroxin", 12,new Ablaufdatum(2025,10,25),3);
        service.addMedikament(para);
        service.addMedikament(new Medikament("01111111", "paracetamol", 12,new Ablaufdatum(2025, 8, 25), 3));
        service.addMedikament(ibu);service.addMedikament(eff);service.addMedikament(pen);service.addMedikament(levo);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1,0})
    void testAddMedThrowExceptionFalseValue(int menge){
        assertThrows(IllegalArgumentException.class,
                () ->service.addMedikament(new Medikament("20250928", "TestMed", 20,new Ablaufdatum(2025,12,12),menge )));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, Integer.MAX_VALUE})
    void testAddMed(int menge){
        service.addMedikament(new Medikament("20250928", "TestMed", 20,new Ablaufdatum(2025,12,12),menge ));
    }

    @ParameterizedTest
    @ValueSource(ints = { -10,0})
    void testIncreaseCountThrowExceptionFalseValue(int count) {
        assertThrows(IllegalArgumentException.class,
                () ->service.increaseCount("04324188",count, new Ablaufdatum(2026, 1, 1)));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, Integer.MAX_VALUE})
    void testIncreaseCount(int count) {
        service.increaseCount("04324188",count, new Ablaufdatum(2026, 1, 1));
    }

    @ParameterizedTest
    @ValueSource(strings = { "p", " 25FGHJK"})
    void testIncreaseCountThrowExceptionNotExistentPzn(String pzn) {
        assertThrows(IllegalArgumentException.class,
                () -> service.increaseCount(pzn,5, new Ablaufdatum(2026, 1, 1)));
    }

    @Test
    void testIncreaseCountQuantityShouldBeUpdated() {
        service.increaseCount("04324188", 10, new Ablaufdatum(2026, 1, 1));
        int bestand = service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188"))
                .mapToInt(m -> m.bestand())
                .sum();
        assertThat(bestand).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1,0})
    void testSellThrowExceptionFalseValue(int count){
        assertThrows(IllegalArgumentException.class,()-> service.sell("01126111", count));
    }

    @ParameterizedTest
    @CsvSource({
            // pzn, count
            "04324188, 1",
            "04324188, 0",
            "04325189, -1",
            "p, -10",
            "p, 10"})

    void testSellThrowExceptionForBadPznOrValue(String pzn,int count){
        assertThrows(IllegalArgumentException.class,()-> service.sell(pzn, count));
        //System.out.println(service.printLager());
    }

    @Test
    void testSellThrowExceptionNotEnoughMed() {
        assertThrows(IllegalArgumentException.class,()-> service.sell("04324188", 2));
        int bestand = service.getMedikamente().stream()
                .filter(m -> m.getTyp().getPzn().equals("04324188"))
                .mapToInt(m -> m.bestand())
                .sum();
        assertThat(bestand).isEqualTo(0);
    }

    @Test
    void testGetMhd_shouldReturnCorrectDate() {
        Ablaufdatum ablauf = new Ablaufdatum(2025, 12, 31);
        assertThat(ablauf.getMhd()).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void testIsExpired_shouldReturnTrueForPastDate() {
        Ablaufdatum ablauf = new Ablaufdatum(2020, 1, 1);
        assertThat(ablauf.isExpired()).isTrue();
    }

    @Test
    void testIsExpired_shouldReturnFalseForFutureDate() {
        Ablaufdatum ablauf = new Ablaufdatum(LocalDate.now().getYear() + 1, 1, 1);
        assertThat(ablauf.isExpired()).isFalse();
    }

}
