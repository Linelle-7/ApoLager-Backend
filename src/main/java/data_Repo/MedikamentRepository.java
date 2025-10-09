package data_Repo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import service.Statistik;
import model.Medikament;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class MedikamentRepository { // TODO Methode die explizit nach mhd sortiert anstatt treemap kompakt zu nutzen.
    private Map< String, TreeMap<LocalDate, Medikament>> lager =new HashMap<>();
    private Map<String, Statistik> statistik = new HashMap<>();
    private Statistik getStatistik(String pzn) {
        return statistik.computeIfAbsent(pzn, k -> new Statistik( pzn));
    }

    // Medikament speichern oder aufstocken
    public void save( Medikament m){
        TreeMap<LocalDate, Medikament> med=lager.computeIfAbsent(m.getPzn(), k-> new TreeMap<>());
        med.put(m.ablaufsdatum(), m);
        getStatistik(m.getPzn()).addGekauft(m.bestand());
        getStatistik(m.getPzn()).setName(m.name());
    }

    private int count(TreeMap<LocalDate, Medikament> list){
        Medikament medInstance;
        int total=0;
        Iterator <Map.Entry<LocalDate, Medikament>> itList =list.entrySet().iterator();
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
        //System.out.println( "Sell222 läuft.....");

        TreeMap<LocalDate, Medikament> meds = findByPzn2(pzn);
        // Prueft ob Dieses medikament ueberhaupt im Lager enthalten ist.
        if (meds == null) {
            throw new IllegalArgumentException("Medikament mit dem Pharmazentralnummer " + pzn + " nicht gefunden!");
        }
        //Pruefe, ob gewünschte Menge einem gültigen Eintrag ist.
        if(menge<=0){
            throw new IllegalArgumentException("Fehlerhafte Eintrag " + menge + " ist kein gueltigen Eintrag!");
        }

        int toSell = menge;
        int capacity = count(meds);

        if( capacity >= menge){
            while ( !meds.isEmpty()) {
                Map.Entry<LocalDate, Medikament> entry = meds.firstEntry();
                Medikament charge = entry.getValue();
                int available= charge.bestand();

                // Instance von Med kleiner als gewünscht? das erstmal nehmen und restlichen Stück in der nächsten Instanz"
                if (available <= toSell) {
                    // komplette Charge verkaufen
                    charge.verkaufen(available);
                    toSell -= available;
                    meds.remove(entry.getKey()); // leere Charge raus
                } else {
                    charge.verkaufen(toSell);
                    break;
                }
            }
            getStatistik(pzn).addVerkauft(menge);
            return capacity-menge;
        }else{
                throw new IllegalArgumentException("Nicht genug Bestand für das Medikament mit dem Pharmazentralnummer " + pzn+". Zu verkaufen " + menge+ " aber aktuell "+ capacity+ " im Bestand.");

        }
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

    // Gesamtes Lager
    public Map<String, TreeMap<LocalDate, Medikament>> findAll() { return lager; }

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

    public PdfPTable shareData(){ //TODO Noch verbessern
        PdfPTable table = new PdfPTable(5);
        // Kopfzeile
        Stream.of("ID", "Produkt", "Gesamt", "Verkauft", "Verworfen")
                .forEach(title -> {
                    PdfPCell header = new PdfPCell(new Phrase(title));
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    table.addCell(header);
                });

        Set<String> keySet=lager.keySet();
        for(String s: keySet){
            Statistik.addRows(table,s,getStatistik(s).getName(), getStatistik(s).getGekauft(),getStatistik(s).getVerkauft(),
                    getStatistik(s).getVerworfen());
        }
        return table;
    }

    public Map< String,Integer> checkQuantityInSoftware(   String id, int qty){
        if(!existsByPzn(id)){
            Map<String, Integer> map= new HashMap <>();
            map.put("", -1);
            return map;
        }
        Set<String> keySet=lager.keySet();
        for(String s: keySet){
            if ( id.equals(s)){
                int tmpQty=getStatistik(s).getGekauft()-getStatistik(s).getVerkauft()-getStatistik(s).getVerworfen();
                if(! (qty==tmpQty)){
                    Map<String, Integer> map= new HashMap <>();
                    map.put(id, tmpQty);
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

    // 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

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

