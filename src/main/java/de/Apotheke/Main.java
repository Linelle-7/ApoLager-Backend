package de.Apotheke;

import data_Repo.MedikamentRepository;
import model.Ablaufsdatum;
import model.Medikament;
import service.LagerService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        //  Service initialisieren
        LagerService service = new LagerService();
        Medikament para=new Medikament("04324188","Paracetamol", 5, new Ablaufsdatum (2025,10,1),5);
        Medikament ibu=new Medikament("01126111","Ibuprofen", 10, new Ablaufsdatum (2025,9,15),26);
        Medikament eff=new Medikament("01126122","Efferalgan", 20, new Ablaufsdatum (2025,6,5),20);
        Medikament pen=new Medikament("02126121","Penicillin", 10, new Ablaufsdatum (2025,1,22),30);
        Medikament levo=new Medikament("04325189","Levotyroxin", 12, new Ablaufsdatum (2025,8,25),3);
        //System.out.println(para.haltbarkeitsdatum());
        service.addMedikament(para);service.addMedikament(ibu);service.addMedikament(eff);service.addMedikament(pen);service.addMedikament(levo);
        //System.out.println("Einfügen \n"+ service.toString());

        System.out.println(service.printLager());
        // Medikamente einkaufen
        service.increaseCount("04324188", 100, new Ablaufsdatum (2026, 5, 1));
        service.increaseCount("04324188", 50,new Ablaufsdatum (2026, 12, 31));
        service.increaseCount("01126111", 200, new Ablaufsdatum (2027, 1, 1));

        //System.out.println("Nach Increase \n"+ service.toString());

        // Medikamente verkaufen
        int remainingCount =service.sell("04324188", 30);

        // 30 Aspirin verkauft
        service.sell("01126111", 20);   // 20 anderes Medikament verkauft
        System.out.println(service.printLager());

//        //System.out.println("Nach Increase und Sell \n" +service.toString());
//        try {
//            service.statistik();
//        }catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        //prüfe, Inventur
//        service.inventur();
        }
    }