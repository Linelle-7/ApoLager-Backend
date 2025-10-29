package service;

import data_Repo.MedikamentRepository;

import java.io.*;
import java.util.*;

public class Inventur {
    private MedikamentRepository repo;
    public Inventur(MedikamentRepository repo) {this.repo = repo;}

    private static Map< String,Integer> checkQuantityInSoftware(  MedikamentRepository repo, String pzn, int qty){
        Set<String> keySet= repo.getAllKeys();
        if(!repo.existsByPzn(pzn)){
            Map<String, Integer> map= new HashMap<>();
            map.put(pzn, -1);
            return map;
        }
        for(String s: keySet){
            if ( pzn.equals(s)){
                int tmpQty=repo.getStatistik(s).getGekauft()-repo.getStatistik(s).getVerkauft()-repo.  getStatistik(s).getVerworfen();
                if(! (qty==tmpQty)){
                    Map<String, Integer> map= new HashMap <>();
                    map.put(pzn, tmpQty);
                    return map;
                }
            }
        }
        return null;
    }

    public static void  vergleicheCsvUndSoftware(MedikamentRepository repo){
        String dateiPfad = "inventur.csv"; // Pfad zur CSV-Datei
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dateiPfad))) {
            String line;
            System.out.println("Die ungleiche Einträge sind:");
            while ((line = br.readLine()) != null) {
                // CSV-Spalten trennen (z.B. ; oder ,)
                String[] parts = line.split(";");
                String pzn = parts[0];
                int menge = Integer.parseInt(parts[1]);
                Map<String, Integer> result=checkQuantityInSoftware( repo,pzn, menge);
                    if( result!=null) {
                        Map.Entry<String, Integer> entry = result.entrySet().iterator().next();
                        parts[1]=String.valueOf(entry.getValue());
                        System.out.println( entry.getKey() + " ; " + entry.getValue());
                }
                line = String.join(";", parts);
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // CSV Datei aktualisieren
       /* try (BufferedWriter bw = new BufferedWriter(new FileWriter(dateiPfad))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }
}
