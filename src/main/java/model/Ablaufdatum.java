package model;

import java.time.LocalDate;

public class Ablaufdatum implements Comparable<Ablaufdatum>  {
    private LocalDate mhd;

    public Ablaufdatum(int jahr, int monat, int tag){
        mhd=LocalDate.of(jahr,monat,tag);
    }

    public LocalDate getMhd(){
        return mhd;
    }

    public boolean isExpired() {
        return mhd.isBefore(LocalDate.now());
    }

    // Hilfsmethode für Tests oder andere Klassen
    public static Ablaufdatum now() {
        LocalDate heute = LocalDate.now();
        return new Ablaufdatum(heute.getYear(), heute.getMonthValue(), heute.getDayOfMonth());
    }
    @Override
    public int compareTo(Ablaufdatum other) {
        return this.getMhd().compareTo(other.mhd);
    }
    @Override
    public String toString(){
        return  this.getMhd().getYear()+"-"+this.getMhd().getMonthValue() + "-"+this.getMhd().getDayOfMonth();
    }

}
