package data_Repo;
import service.Statistik;
import model.Medikament;

import java.time.LocalDate;
import java.util.*;

public class MedikamentRepository { // ......TODO Methode die explizit nach mhd sortiert anstatt treemap kompakt zu nutzen.
    private Map< String, TreeMap<LocalDate, Medikament>> lager =new HashMap<>();
    private Map<String, Statistik> statistik = new HashMap<>();
    private Statistik getStatistik(String pzn) {
        return statistik.computeIfAbsent(pzn, k -> new Statistik( pzn));
    }

    // Medikament speichern oder aufstocken
    public void save( Medikament m){
        TreeMap<LocalDate, Medikament> med=lager.computeIfAbsent(m.getPzn(), k-> new TreeMap<>());
        med.put(m.ablaufsdatum(), m);

        // zustand dieses Medikament in Statistik aktualisieren.
        getStatistik(m.getPzn()).addGekauft(m.bestand());
        getStatistik(m.getPzn()).setName(m.name());
    }

    public int count(String pzn){
        TreeMap<LocalDate, Medikament> meds= findByPzn2(pzn);
        Medikament medInstance;
        int total=0;
        Iterator <Map.Entry<LocalDate, Medikament>> itList =meds.entrySet().iterator();
        while(itList.hasNext()){
            medInstance=itList.next().getValue();
            // Nur
            if(medInstance.ablaufsdatum().isAfter(LocalDate.now())){
                total+=medInstance.bestand();
            }else{
                getStatistik(medInstance.getPzn()).addVerwerfen(medInstance.bestand());
                //delete(medInstance.getPzn(), medInstance.ablaufsdatum());
                itList.remove();
            }
        }
        return total;
    }

    private TreeMap<LocalDate, Medikament> findByPzn2(String pzn) {
        TreeMap<LocalDate, Medikament> charges = lager.get(pzn);
        if (charges == null) return null;
        return charges;
    }

    public int sellMed( String pzn, int menge){
        TreeMap<LocalDate, Medikament> meds = findByPzn2(pzn);
        int toSell = menge;
        int capacity = count(pzn);
        while ( !meds.isEmpty()) {
            Map.Entry<LocalDate, Medikament> entry = meds.firstEntry();
            Medikament charge = entry.getValue();
            int available= charge.bestand();

            if (available <= toSell) { // Instance von Med kleiner als gewünscht? das erstmal nehmen und restlichen Stück in der nächsten Instanz"
                // komplette Charge verkaufen
                charge.verkaufen(available);
                toSell -= available;
                meds.remove(entry.getKey()); // leere Charge raus
            } else {
                charge.verkaufen(toSell);
                break;
            }
        }
            getStatistik(pzn).addVerkauft(menge); // zustand dieses Medikament in Statistik aktualisieren.
            return capacity-menge;
    }

    // Ein bestimmtes Medikament mithilfe pzn und Ablaufsdatum finden
    public Medikament findByPznAndAblauf( String pzn, LocalDate ablauf){
        TreeMap<LocalDate, Medikament> medInstance=lager.get(pzn);
        if( medInstance==null ) return null;
        return medInstance.get(ablauf);
    }

    // Alle Chargen einer PZN
    public Collection<Medikament> findByPzn(String pzn) {
        TreeMap<LocalDate, Medikament> charges = lager.get(pzn);
        if (charges == null) return Collections.emptyList();
        return charges.values();
    }

    // Entfernen einer Charge
    public void delete(String pzn, LocalDate ablauf) {
        TreeMap<LocalDate, Medikament> charges = lager.get(pzn);
        if (charges != null) {
            charges.remove(ablauf);
            if (charges.isEmpty()) {
                lager.remove(pzn); // falls keine Charge mehr übrig bleibt
            }
        }
    }

    // Prüfen, ob PZN im Lager existiert
    public boolean existsByPzn(String pzn) {
        return lager.containsKey(pzn);
    }

    public  String[][] collectDataForStatistik(){
        Set<String> keySet=lager.keySet();
        int n=keySet.size();
        String[][] recap=new String[n][5];
        int i=0;
        for(String s: keySet){
            recap[i][0]=s;
            recap[i][1]=getStatistik(s).getName();
            recap[i][2]=""+getStatistik(s).getGekauft();
            recap[i][3]=""+getStatistik(s).getVerkauft();
            recap[i++][4]= ""+getStatistik(s).getVerworfen();
        }
        return recap;
    }

    public Map< String,Integer> checkQuantityInSoftware(   String pzn, int qty){
        if(!existsByPzn(pzn)){
            Map<String, Integer> map= new HashMap <>();
            map.put(pzn, -1);
            return map;
        }
        Set<String> keySet=lager.keySet();
        for(String s: keySet){
            if ( pzn.equals(s)){
                int tmpQty=getStatistik(s).getGekauft()-getStatistik(s).getVerkauft()-getStatistik(s).getVerworfen();
                if(! (qty==tmpQty)){
                    Map<String, Integer> map= new HashMap <>();
                    map.put(pzn, tmpQty);
                    return map;
                }
            }
        }
        return null;
    }

    public String printlager() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Lagerbestand ===\n");

        for (String pzn : lager.keySet()) {
            sb.append("PZN ").append(pzn).append(":\n");
            TreeMap<LocalDate, Medikament> charges = lager.get(pzn);

            for (Medikament m : charges.values()) {
                sb.append("   ").append(m).append("\n"); // nutzt Medikament.toString()
            }
        }

        return sb.toString();
    }

    public ArrayList<Medikament> getMedikamente() {
        ArrayList<Medikament> list = new ArrayList<>();

        for (String pzn : lager.keySet()) {
            TreeMap<LocalDate, Medikament> medInstance = findByPzn2(pzn);
            assert medInstance != null;
            list.addAll(medInstance.values());
        }
        return list;
    }

}

