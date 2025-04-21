package cr.ac.una.taskprogramll.util;

import cr.ac.una.taskprogramll.model.Team;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import java.io.IOException;

/** * * * @author ashly */
public class Certificate {
    
    private static final String BASE_PATH = "/cr/ac/una/taskprogramll/resources/Certificates/";
        
    public static void certificate(Team winner, String tourney) {
        String direction = BASE_PATH + winner.getName() + "_" + tourney+ ".pdf";
        String certificateBackground = BASE_PATH + "CertificateImage.png";
        String winnerImage = BASE_PATH + winner.getId()+ ".png";
        Document document;
        try {
            PdfWriter writePdf = new PdfWriter(direction);
            PdfDocument pdf = new PdfDocument(writePdf);
            PageSize pdfSize = new PageSize(549, 303);

            pdf.setDefaultPageSize(pdfSize);
            document = new Document(pdf);

            ImageData backgroundImage = ImageDataFactory.create(certificateBackground);
            PdfCanvas canva = new PdfCanvas(pdf.addNewPage());
            canva.addImage(backgroundImage, 0, 0, pdfSize.getWidth(), false);

            ImageData winnerImageData = ImageDataFactory.create(winnerImage);
            canva.addImage(winnerImageData, new Rectangle(420, 100, 115, 91), false);

            Paragraph teamName = new Paragraph("Equipo: " + winner.getName())
                    .setFontSize(14)
                    .setMarginTop(97)
                    .setMarginLeft(35);

            Paragraph sport = new Paragraph("Deporte: " + winner.searchSportType().getName())
                    .setFontSize(14)
                    .setMarginTop(6)
                    .setMarginLeft(35);

            Paragraph points = new Paragraph("Puntos: " + winner.getPoints())
                    .setFontSize(14)
                    .setMarginTop(6)
                    .setMarginLeft(35);

            document.add(teamName);
            document.add(sport);
            document.add(points);

            document.close();
        } catch (IOException e) {
            System.out.println("Error:" + e);
        }
    }
}
