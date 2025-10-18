package model;

import java.time.LocalDate;

public class MedikamentenPackung {
    private MedikamentenTyp medTyp;
    private LocalDate mhd;
    private int bestand;



    public MedikamentenPackung(MedikamentenTyp typ, LocalDate date, int menge ){
        medTyp=typ;
        mhd=date;
        bestand=menge;
    }

    public MedikamentenTyp getTyp() { return medTyp; }
    public LocalDate ablaufsdatum(){ return mhd;}
    public int bestand(){ return bestand;}
    public void aufstocken(int menge){ bestand += menge; }
    public void verkaufen (int menge){ bestand -=menge; }

    @Override
    public String toString(){
        return "Bestand: " +bestand + " Ablaufdatum: "+ mhd;
    }

    public String printpackung() {
        return medTyp.name() + " | MHD: " + mhd+ " | Bestand: " + bestand;
    }
}
