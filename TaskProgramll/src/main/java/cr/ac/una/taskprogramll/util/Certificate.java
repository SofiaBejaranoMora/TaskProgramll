//package cr.ac.una.taskprogramll.util;
//
//import cr.ac.una.taskprogramll.model.Team;
//
///** * * * @author ashly */
//public class Certificate {
//    
//    private static final String BASE_PATH = "/cr/ac/una/taskprogramll/resources/";
//        
//    public void userCard(Team winner, String tourney) {
//        String direction = BASE_PATH + winner.getName() + "_" + tourney+ ".pdf";
//        String backgroundImage = BASE_PATH+ 1 +"png";
//        String winnerImage = BASE_PATH + winner.getId()+ ".png";
//        Document document;
//        try {
//            PdfWriter writePdf = new PdfWriter(direction);
//            PdfDocument pdf = new PdfDocument(writePdf);
//            PageSize pdfSize = new PageSize(549, 303);
//
//            pdf.setDefaultPageSize(pdfSize);
//            document = new Document(pdf);
//
//            ImageData backgroundImage = ImageDataFactory.create(backgroundImage);
//            PdfCanvas canva = new PdfCanvas(pdf.addNewPage());
//            canva.addImage(backgroundImage, 0, 0, pdfSize.getWidth(), false);
//
//            ImageData imageDataUser = ImageDataFactory.create(winnerImage);
//            canva.addImage(imageDataUser, new Rectangle(420, 100, 115, 91), false);
//
//            Paragraph paragraphName = new Paragraph("Nombre: " + associated.getName() + " " + associated.getLastName() + " " + associated.getSecondLastName())
//                    .setFontSize(14)
//                    .setMarginTop(97)
//                    .setMarginLeft(35);
//
//            Paragraph paragraphAge = new Paragraph("Edad: " + associated.getAge())
//                    .setFontSize(14)
//                    .setMarginTop(6)
//                    .setMarginLeft(35);
//
//            Paragraph paragraphFolio = new Paragraph("Folio: " + associated.getFolio())
//                    .setFontSize(14)
//                    .setMarginTop(6)
//                    .setMarginLeft(35);
//
//            document.add(paragraphName);
//            document.add(paragraphAge);
//            document.add(paragraphFolio);
//
//            document.close();
//        } catch (IOException e) {
//            System.out.println("Error:" + e);
//        }
//    }
//}
