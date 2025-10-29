package util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class PdfManager {

    public PdfPTable create_FillTable(String[][] data){
        PdfPTable table = new PdfPTable(5);
        // Kopfzeile einfügen
        addTableHeader(table);
        for( int i=0; i<data.length; i++){
            addRows(table,data[i][0],data[i][1],data[i][2],data[i][3],data[i][4]);
        }
        return table;
    }

    // Code aus https://www.baeldung.com/java-pdf-creation kopiert und entsprechend angepasst.
    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Produkt", "Gesamt", "Verkauft", "Verworfen")
                .forEach(title -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(title));
                    table.addCell(header);
                });
    }

    private static void addRows(@NotNull PdfPTable table, String pzn, String name, String gesamt, String verkauft, String verworfen) {
        table.addCell(pzn);
        table.addCell(name);
        table.addCell(gesamt);
        table.addCell (verkauft);
        table.addCell(verworfen);
    }
}
