package de.Apotheke;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class LagerService {

    private MedikamentRepository repo;
    public LagerService(MedikamentRepository repository) {this.repo = repository;}

    public void addMedikament(Medikament m) {
        repo.save(m);
    }

    public void increaseCount(String pzn, int menge, LocalDate ablauf) {
        //System.out.println( "Increase laüft.....");
        Medikament charge = repo.findByPznAndAblauf(pzn, ablauf);
        if (charge != null) {
            charge.aufstocken(menge);
        } else {
            // Existenz mEdikament mit gegebene pzn prüfen
            if (!repo.existsByPzn(pzn)) {
                throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " existiert nicht!");
            }
            // Wenn Pzn existiert aber nur keine Instanz mit dem gegebenen Ablaufdatum hat, dann neue instanz mit diesem Datum erzeugen
            Medikament ref = repo.findByPzn(pzn).iterator().next();
            Medikament neu = new Medikament(pzn, ref.name(),ref.price(), ablauf,menge);
            repo.save(neu);
        }
    }

    // Verkauf (FIFO: frühestes Ablaufdatum zuerst)
    public int sell(String pzn, int menge) {
        //System.out.println( "Sell läuft.....");
       return repo.selling(pzn,menge);
    }

    public void statistik() throws DocumentException, IOException {
        Statistik stats= new Statistik(this.repo);
        stats.statistik();
    }

    public void inventur()  {
        Inventur inv= new Inventur(this.repo);
        inv.inventurAbgleich();
    }

    public String toString(){
        return repo.toString();
    }

    public ArrayList<Medikament> getMedikamente() {
        return repo.getMedikamente();
    }
}
