package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import data_Repo.MedikamentRepository;
import model.Statistik;
import util.PdfManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class StatistikService {

    public static void getStatistik( MedikamentRepository repo) throws DocumentException, IOException {
        Document document = new Document();
        // Writer verknüpfen: sagt dem Document, wohin geschrieben wird
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateiname = "statistik_" + now.format(formatter) + ".pdf";
        FileOutputStream fos=new FileOutputStream(dateiname);

        PdfWriter.getInstance(document,fos );
        document.open();
        Paragraph p=new Paragraph( "Statistik am "+ now +" erstellt\n");
        p.setAlignment( Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" ")); // damit sichere ich, dass die Tabelle der in Datei zuerst geschriebenen Paragraf nicht beschädigt/ versteckt.

        String[][] data = collectDataForStatistik(repo);
        PdfPTable table=new PdfManager().create_FillTable(data); // Methode aus Klasse PdfManager der Util Package, die Table erzeugt und mit gegebenen Daten ausfüllt und zurückgibt.
        document.add(table);
        document.close();
        fos.close();
    }

    private static String[][] collectDataForStatistik(MedikamentRepository repo){
        Map<String, Statistik> stats=repo.getStatistik();
        Set<String> keySet=stats.keySet();
        int n=keySet.size();
        String[][] recap=new String[n][5];
        int i=0;
        for(String s: keySet){
            recap[i][0]=s;
            recap[i][1]=repo.getStatistik(s).getName();
            recap[i][2]=""+repo.getStatistik(s).getGekauft();
            recap[i][3]=""+repo.getStatistik(s).getVerkauft();
            recap[i++][4]= ""+repo.getStatistik(s).getVerworfen();
        }
        return recap;
    }
}
