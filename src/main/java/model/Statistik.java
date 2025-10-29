package model;

import data_Repo.MedikamentRepository;

public class Statistik {
    private String pzn;
    private String name;
    private int gekauft;
    private int verkauft;
    private int verworfen;

    private MedikamentRepository repo;
    public Statistik(MedikamentRepository repo) {
        this.repo = repo;
    }

    public Statistik( String pzn) {
        this.pzn = pzn;
    }
    public void setStatus(int menge, String kategorie){
        if( kategorie.equals("buy")){
            this.gekauft+=menge;
        } else if(kategorie.equals("sale")){
            this.verkauft+=menge;
        }else{
            this.verworfen+=menge;
        }
    }

    public int getGekauft() { return gekauft; }
    public void addGekauft(int menge) { gekauft += menge; }

    public int getVerkauft() { return verkauft; }
    public void addVerkauft(int menge) { verkauft += menge; }

    public int getVerworfen() { return verworfen; }
    public void addVerwerfen(int menge) { verworfen += menge; }

    public String getName(){return name;}
    public void setName(String n){ name=n;}



}
