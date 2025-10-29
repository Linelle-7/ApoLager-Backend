package service;

import com.itextpdf.text.DocumentException;
import data_Repo.MedikamentRepository;
import model.Ablaufdatum;
import model.Medikament;

import java.io.IOException;
import java.util.*;

public class LagerService {

    private MedikamentRepository repo;
    public LagerService() {
        this.repo = new MedikamentRepository();}

    public void addMedikament(Medikament m) {
        repo.save(m);
    }

    public void increaseCount(String pzn, int menge, Ablaufdatum ablauf) {
        // Existenz Medikament mit gegebene pzn prüfen
        if (!repo.existsByPzn(pzn)) {
            throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " existiert nicht!");
        } else {
            // Wenn Pzn existiert aber nur keine Instanz mit dem gegebenen Ablaufdatum hat, dann neue instanz mit diesem Datum erzeugen
            Medikament neu = new Medikament( repo.getTyp(pzn), ablauf,menge);
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
            // jetzt prüfen, ob die Anforderung erfüllt sein kann
            int capacity = Verkaufservice.count(repo,pzn);
            if(capacity>=menge){
                return Verkaufservice.sellMed(repo,pzn,menge);
            }else{
                throw new IllegalArgumentException("Nicht genug Bestand für das Medikament mit dem Pharmazentralnummer " + pzn+". Zu verkaufen " + menge+ " aber aktuell "+ capacity+ " im Bestand.");
            }
        }else{
                throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " nicht gefunden!");
        }
    }

    public void statistik() throws DocumentException, IOException {
        StatistikService.getStatistik(repo);
    }

    public void inventur()  {
        Inventur.vergleicheCsvUndSoftware(repo);
    }

    public String printLager(){
        return repo.toString();
    }

    public ArrayList<Medikament> getMedikamente() {
        return repo.getMedikamente();
    }
}
