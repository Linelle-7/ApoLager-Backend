package model;

import java.time.LocalDate;

public class Ablaufsdatum {
    private LocalDate mhd;


    public Ablaufsdatum( int jahr, int monat, int tag){
        mhd=LocalDate.of(jahr,monat,tag);
    }

    public LocalDate getMhd(){
        return mhd;
    }
}
