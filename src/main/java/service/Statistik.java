package service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import data_Repo.MedikamentRepository;
import util.PdfManager;


import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public void addGekauft(int menge) { gekauft += menge; }
    public void addVerkauft(int menge) { verkauft += menge; }
    public void addVerwerfen(int menge) { verworfen += menge; }
    public void setName(String n){ name=n;}

    public int getGekauft() { return gekauft; }
    public int getVerkauft() { return verkauft; }
    public int getVerworfen() { return verworfen; }
    public String getName(){return name;}

    public void statistik() throws DocumentException, IOException {
        Document document = new Document();
        // Writer verknüpfen: sagt dem Document, wohin geschrieben wird
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateiname = "statistik_" + now.format(formatter) + ".pdf";
        FileOutputStream fos=new FileOutputStream(dateiname);

        PdfWriter.getInstance(document,fos );
        document.open();
        Paragraph p=new Paragraph( "Statistik am "+ now +"\n");
        p.setAlignment( Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" ")); // damit sichere ich, dass es Tabelle die oben geschriebene Paragraph nicht beschädigt/ versteckt.

        String[][] data = repo.collectDataForStatistik();
        PdfPTable table=new PdfManager().CreateAndInsertdata(data); // Methode aus Klasse PdfManager derUtil Package, die Table erzeugt und mit gegebenen Daten ausfüllt und zurückgibt.
        document.add(table);
        document.close();
        fos.close();
    }

}
