package model;

public class Medikament {

    private MedikamentenTyp medTyp;
    private Ablaufdatum mhd;
    private int bestand;

    //Konstruktoren
    public Medikament(MedikamentenTyp medTyp, Ablaufdatum mhd, int menge){
        if (menge>0){
            this.medTyp=medTyp;
            this.mhd=mhd;
            this.bestand=menge;
        }else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }
    }

    public Medikament(String pzn, String name, double price, Ablaufdatum mhd, int menge) {
        if (menge > 0) {
            this.medTyp= new MedikamentenTyp(pzn, name, price);
            this.mhd=mhd;
            this.bestand=menge;
        } else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }
    }

    public MedikamentenTyp getTyp() { return medTyp; }
    public Ablaufdatum ablaufdatum(){ return mhd;}
    public int bestand(){ return bestand;}
    public void updateQuantity(int menge){ bestand += menge; }

    public boolean isExpired() {
        return this.ablaufdatum().isExpired();
    }

    @Override
    public String toString() {
        return "Medikament{" +
                this.getTyp().toString() +  "Bestand: " +this.bestand + " Ablaufdatum: "+ this.ablaufdatum() +'}';
    }

}
