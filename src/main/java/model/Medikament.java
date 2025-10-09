package model;
import java.time.LocalDate;

public class Medikament {
    // Innere Exception Class.
    private class NotEnoughOrSuchElementException extends RuntimeException {
        public NotEnoughOrSuchElementException(String s) {
            super(s);
        }
    }

    // Instanzvar
    private String pzn ; //pharmazentralNummer
    private String serienNummer;
    private String name;
    private double price;
    private LocalDate mhd;
    private int  bestand;
    private int gesamtzahl;
    private int abgelaufen;
    private int verkauft;

    //Packung abhängige ...........

    //Konstruktor
    public Medikament(String pzn, String name, double price, LocalDate mhd, int menge){
        if(menge<0){
            menge*=-1;
        }
        this.pzn=pzn;
        this.name=name;
        this.price=price;
        this.bestand=menge;
        this.gesamtzahl=menge;
        this.mhd=mhd;
    }

    // Instanzmethoden
    public String name(){ return name;}
    public String getPzn(){ return pzn;}
    public int bestand(){ return bestand;}
    public LocalDate ablaufsdatum(){ return mhd;}
    public double price(){ return price;}
    public void changePrice( double p){ this.price=p;}
    public void aufstocken(int menge){ bestand += menge;gesamtzahl +=menge; }

    public void verkaufen (int menge){
        if (bestand<menge){
            throw new NotEnoughOrSuchElementException("Wanted; " +menge+  " aber nur: "+ bestand);
        }
        bestand -=menge;
        verkauft+=menge;
    }

    @Override
    public String toString() {
        return "Medikament{" +
                "pzn='" + pzn + '\'' +
                ", name='" + name + '\'' +
                ", preis=" + price +
                ", mhd=" + mhd +
                ", bestand=" + bestand +
                '}';
    }

}
