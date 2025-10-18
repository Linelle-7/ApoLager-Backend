package model;
import java.time.LocalDate;

public class Medikament {

    private MedikamentenTyp typ;
    private MedikamentenPackung packung;

    //Konstruktoren
    public Medikament(MedikamentenTyp medTyp, LocalDate  mhd, int menge){
        if (menge>0){
            typ=medTyp;
            packung=new MedikamentenPackung(typ, mhd, menge);
        }else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }

    }

    public Medikament(String pzn, String name, double price, LocalDate mhd, int menge) {
        if (menge > 0) {
            typ = new MedikamentenTyp(pzn, name, price);
            packung = new MedikamentenPackung(typ, mhd, menge);

        } else {
            throw new IllegalArgumentException("Fehlerhafte Eintrag. " + menge + " ist kein gueltigen Eintrag!");
        }
    }


    //public MedikamentenTyp getTyp() { return typ; }
    public MedikamentenPackung  getPackung() { return packung; }

    @Override
    public String toString() {
        return "Medikament{" +
                typ.toString() + packung.toString() +
                '}';
    }
}
