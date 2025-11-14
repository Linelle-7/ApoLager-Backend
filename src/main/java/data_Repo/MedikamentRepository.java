package data_Repo;
import model.Ablaufdatum;
import model.MedikamentenTyp;
import model.Statistik;
import model.Medikament;
import java.util.*;

//TODO Medikament in json speichern, Komplexe Datentyps? DTO erzeugen und dann in Json
// Prüfen, ob PZN im Lager existiert

public class MedikamentRepository {
    private Map<String, TreeMap<Ablaufdatum, Medikament>> lager = new HashMap<>();

    //alle erforderliche Methoden, um Zahlen safe und schrittweise für die STatistik zu speichern und am Ende zu holen.
    private Map<String, Statistik> statistik = new HashMap<>();
    public Statistik getStatistik(String pzn) {
        return statistik.computeIfAbsent(pzn, k -> new Statistik(pzn));
    }
    public Map<String, Statistik> getStatistik(){ return statistik;}

    // MedikamentenTyp teilten
    public MedikamentenTyp getTyp(String pzn) {
        TreeMap<Ablaufdatum,Medikament> charges = lager.get(pzn);
        if (charges == null) return null;
        return charges.firstEntry().getValue().getTyp();
    }

    // Medikament speichern oder aufstocken
    public void save(Medikament m) {
        // Wenn Medikament mit diesem pzn noch nicht enthalten ist, dann einfach eine neue Treemap erstellen und dort hinzufügen.
        TreeMap<Ablaufdatum, Medikament> meds = lager.computeIfAbsent(m.getTyp().getPzn(), k -> new TreeMap<>());

        Ablaufdatum mhd = m.ablaufdatum();
        if (meds.containsKey(mhd)) {
            meds.get(mhd).updateQuantity(m.bestand()); //existiert es schon was mit selben pzn und ablaufdatum? bestand einfach hochzahlen
        } else {
            meds.put(m.ablaufdatum(), m); //selben pzn aber Ablaufdatum nicht gleich? Treemap dafür erstellen Packung speichern.
        }

        // zustand dieses Medikament in Statistik aktualisieren.
        getStatistik(m.getTyp().getPzn()).setStatus(m.bestand(), "buy");
        getStatistik(m.getTyp().getPzn()).setName(m.getTyp().name());
    }

    public TreeMap<Ablaufdatum, Medikament> findByPzn(String pzn) {
        TreeMap<Ablaufdatum, Medikament> charges = lager.get(pzn);
        if (charges == null) throw new IllegalArgumentException("PZN " + pzn + " existiert nicht im Lager!");
        return charges;
    }

    // Schlüsseln des Lagers geben
    public Set<String> getAllKeys ( ){
        this.refreshLager();
        return lager.keySet();
    }

    // Methode zur Beseitigung einer Packung enes MedikamentenTyp
    public TreeMap<Ablaufdatum, Medikament> checkQuality(TreeMap<Ablaufdatum, Medikament> packung) {
        List<Ablaufdatum> abgelaufen = new ArrayList<>();
        // Abgelaufene Schlüssel sammeln
        for (Ablaufdatum mhd : packung.keySet()) {
            if (mhd.isExpired()) {
                abgelaufen.add(mhd);
            }
        }
        // jetzt löschen
        for (Ablaufdatum mhd : abgelaufen) {
            getStatistik(packung.get(mhd).getTyp().getPzn()).setStatus(packung.get(mhd).bestand(), "expired");
            delete(packung.get(mhd).getTyp().getPzn(), mhd);
        }
        return packung;
    }

    //Methode zum Bereignigen der ganzen Lager
    public void refreshLager(){
        int n=lager.size();
        Map<Ablaufdatum, Medikament> [] tab= new Map[n];
        Set<String> keySet=lager.keySet();
        int index=0;
        for( String pzn: lager.keySet()){
            tab[index++]=lager.get(pzn);
        }
        for(Map<Ablaufdatum, Medikament> entry: tab){
            entry= checkQuality((TreeMap)entry);
        }
    }

    // Entfernen einer Charge
    public void delete(String pzn, Ablaufdatum ablauf) {
        TreeMap<Ablaufdatum,Medikament> medTyp = lager.get(pzn);
        if (medTyp != null) {
            medTyp.remove(ablauf); // Packung mit dem entsprechenden Ablaufdatum verwerfen.
            if (medTyp.isEmpty()) {
                lager.remove(pzn); // Typ löschen, falls keine Packung mehr übrig bleibt
            }
        }
    }

    public boolean existsByPzn(String pzn) {
        return lager.containsKey(pzn);
    }

    public String toString() {
        this.refreshLager();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Lagerbestand ===\n");

        for (String pzn : lager.keySet()) {
            sb.append("PZN ").append(pzn).append(":\n");
            TreeMap<Ablaufdatum,Medikament> charges = lager.get(pzn);
            //charges=checkQuality(charges);
            for (Medikament m : charges.values()) {
                sb.append("   ").append(m.getTyp()).append(m).append("\n"); // nutzt Medikament.toString()
            }
        }
        return sb.toString();
    }

    public ArrayList<Medikament> getMedikamente() {
       this.refreshLager();
        ArrayList<Medikament> list = new ArrayList<>();

        for (String pzn : lager.keySet()) {
            TreeMap<Ablaufdatum,Medikament> packung = findByPzn(pzn);
            //packung=checkQuality(packung);
            assert packung != null;
            list.addAll(packung.values());
        }
        return list;
    }
}