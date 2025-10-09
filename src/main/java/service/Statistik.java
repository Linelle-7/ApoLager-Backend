package service;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import data_Repo.MedikamentRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

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

    //TODO: Anpassungen vornehmen, um Top -down-Kommunikation zu schaffen.

    public void statistik() throws DocumentException, IOException {
        Document document = new Document();
        // Writer verknüpfen: sagt dem Document, wohin geschrieben wird
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateiname = "statistik_" + now.format(formatter) + ".pdf";
        FileOutputStream fos=new FileOutputStream(dateiname);

        PdfWriter.getInstance(document,fos );

        document.open();

        //KopZeile Einfuegen
        //addTableHeader(table);
        // Tabelle aus Medikament repository Holen und in dokument hinzufuegen.
        PdfPTable table = repo.shareData();
        document.add(table);
        document.close();
        fos.close();
    }

    // Code aus https://www.baeldung.com/java-pdf-creation kopiert und entsprechend angepasst.
    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Produkt", "Gesamt", "Verkauft", "Verworfen", "Übrig")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    public static void addRows(PdfPTable table, String apoNum, String name, int gesamt, int verkauft, int verworfen) {
        table.addCell(apoNum);
        table.addCell(name);
        table.addCell(String.valueOf(gesamt));
        table.addCell(String.valueOf(verkauft));
        table.addCell(String.valueOf(verworfen));
        //table.addCell(String.valueOf(uebrig));
    }

}
