package model;
import java.time.LocalDate;

public class Medikament {

    private MedikamentenTyp medTyp;
    private Ablaufsdatum mhd;
    private int bestand;


    //Konstruktoren
    public Medikament(MedikamentenTyp medTyp, Ablaufsdatum  mhd, int menge){
        if (menge>0){
            this.medTyp=medTyp;
            this.mhd=mhd;
            this.bestand=menge;

        }else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }

    }

    public Medikament(String pzn, String name, double price,Ablaufsdatum mhd, int menge) {
        if (menge > 0) {
            this.medTyp= new MedikamentenTyp(pzn, name, price);
            this.mhd=mhd;
            this.bestand=menge;

        } else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }
    }


    public MedikamentenTyp getTyp() { return medTyp; }
    public Ablaufsdatum ablaufsdatum(){ return mhd;}
    public int bestand(){ return bestand;}
    public void updateQuantity(int menge){ bestand += menge; }
    public boolean isExpired() {
        return this.ablaufsdatum().isExpired();
    }

    @Override
    public String toString() {
        return "Medikament{" +
                medTyp.toString() +  "Bestand: " +bestand + " Ablaufdatum: "+ mhd +'}';
    }

}
