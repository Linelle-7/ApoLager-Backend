package model;

import java.time.LocalDate;

public class Ablaufsdatum implements Comparable<Ablaufsdatum>  {
    private LocalDate mhd;


    public Ablaufsdatum( int jahr, int monat, int tag){
        mhd=LocalDate.of(jahr,monat,tag);
    }

    public LocalDate getMhd(){
        return mhd;
    }

    public boolean isExpired() {
        return mhd.isBefore(LocalDate.now());
    }

    // Hilfsmethode für Tests oder andere Klassen
    public static Ablaufsdatum now() {
        LocalDate heute = LocalDate.now();
        return new Ablaufsdatum(heute.getYear(), heute.getMonthValue(), heute.getDayOfMonth());
    }
    @Override
    public int compareTo(Ablaufsdatum other) {
        return this.getMhd().compareTo(other.mhd);
    }

}
