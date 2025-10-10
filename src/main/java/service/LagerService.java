package service;

import com.itextpdf.text.DocumentException;
import data_Repo.MedikamentRepository;
import model.Medikament;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class LagerService {

    private MedikamentRepository repo;
    public LagerService() {
        this.repo = new MedikamentRepository();}

    public void addMedikament(Medikament m) {
        repo.save(m);
    }

    public void increaseCount(String pzn, int menge, LocalDate ablauf) {
        //System.out.println( "Increasing.....");
        // Existenz Medikament mit gegebene pzn prüfen
        if (!repo.existsByPzn(pzn)) {
            throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " existiert nicht!");
        }
        Medikament charge = repo.findByPznAndAblauf(pzn, ablauf); // Medikament mit gleiche pzn und Ablaufsdatum
        if (charge != null) {
            charge.aufstocken(menge);
        } else {
            // Wenn Pzn existiert aber nur keine Instanz mit dem gegebenen Ablaufdatum hat, dann neue instanz mit diesem Datum erzeugen
            Medikament ref = repo.findByPzn(pzn).iterator().next();
            Medikament neu = new Medikament(pzn, ref.name(),ref.price(), ablauf,menge);
            repo.save(neu);
        }
    }

    // Verkauf (FIFO: frühestes Ablaufdatum zuerst)
    public int sell(String pzn, int menge) {
        if(menge<=0){ //Pruefe, ob gewünschte Menge einem gültigen Eintrag ist.
            throw new IllegalArgumentException("Fehlerhafte Eintrag " + menge + " ist kein gueltigen Eintrag!");
        }
        // Prueft ob Dieses medikament ueberhaupt im Lager enthalten ist.
        if (repo.existsByPzn(pzn)){
            // jetzt dann prüefen, ob die Anforderung erfüllt sein kann
            int capacity = repo.count(pzn);
            if(capacity>=menge){
                return repo.sellMed(pzn,menge);
            }else{
                throw new IllegalArgumentException("Nicht genug Bestand für das Medikament mit dem Pharmazentralnummer " + pzn+". Zu verkaufen " + menge+ " aber aktuell "+ capacity+ " im Bestand.");
            }
        }else{
                throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " nicht gefunden!");
        }
    }

    public void statistik() throws DocumentException, IOException {
        Statistik stats= new Statistik(this.repo);
        stats.statistik();
    }

    public void inventur()  {
        Inventur inv= new Inventur(this.repo);
        inv.vergleicheCsvUndSoftware();
    }

    public String printMed(){
        return repo.toString();
    }

    public ArrayList<Medikament> getMedikamente() {
        return repo.getMedikamente();
    }
}
