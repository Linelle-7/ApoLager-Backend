package service;

import data_Repo.MedikamentRepository;
import model.Ablaufdatum;
import model.Medikament;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Verkaufservice {

    public static int count(MedikamentRepository repo,String pzn) {
        TreeMap<Ablaufdatum, Medikament> meds = repo.findByPzn(pzn);
        meds=repo.checkQuality(meds);
        int total = 0;
        Iterator<Map.Entry<Ablaufdatum, Medikament>> itList = meds.entrySet().iterator();
        while (itList.hasNext()) {
            total += itList.next().getValue().bestand();
        }
        return total;
    }

    public static int sellMed(MedikamentRepository repo,String pzn, int menge){
        TreeMap<Ablaufdatum, Medikament> meds = repo.findByPzn(pzn);
        int toSell = menge;
        int capacity = count(repo,pzn);
        while ( !meds.isEmpty()) {
            Map.Entry<Ablaufdatum,Medikament> entry = meds.firstEntry();
            Medikament charge = entry.getValue();
            if (toSell>0) {
                toSell = updateSellingStatus(meds,charge,toSell);
            } else {
                break;
            }
        }
        repo.getStatistik(pzn).setStatus(menge, "sale"); // zustand dieses Medikament in Statistik aktualisieren.
        return capacity-menge;
    }

    private static int updateSellingStatus (TreeMap<Ablaufdatum,Medikament> meds , Medikament charge , int tosell){
        int available= charge.bestand();  //TODO: VerkauftsService
        if (available <= tosell){ // Instance von Med kleiner als gewünscht? das erstmal nehmen und restlichen Stück in der nächsten Instanz"
            charge.updateQuantity(-available); // komplette Charge verkaufen
            tosell -= available;
            meds.remove(charge.ablaufdatum());  // leere Charge raus
        }else{
            charge.updateQuantity(-tosell);
            return 0;
        }
        return tosell;
    }
}
