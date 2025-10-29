package model;

public class MedikamentenTyp {
    private String pzn ; //pharmazentralNummer
    private String name;
    private double price;


    public MedikamentenTyp(String pzn, String name, double price){
        this.pzn=pzn;
        this.name=name;
        this.price=price;
    }

    public String name(){ return name;}
    public String getPzn(){ return pzn;}
    public double price(){ return price;}
    public void updatePrice( double p){ this.price=p;}

    @Override
    public String toString() {
        return  "Name: " +this.name() + ", PZN: " + this.getPzn() + ", Preis: " + this.price() +"€ " ;
    }
}
