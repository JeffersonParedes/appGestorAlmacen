package com.gestoralmacen.app.report;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static final Color COLOR_PRIMARY = new Color(28, 40, 51); // Slate Dark
    public static final Color COLOR_SECONDARY = new Color(208, 211, 212); // Neutral Light Grey
    public static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_PRIMARY);
    public static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
    public static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    public static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

    public static void addHeader(Document document, String title) throws DocumentException {
        Paragraph titleParagraph = new Paragraph(title, FONT_TITLE);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(5);
        document.add(titleParagraph);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateParagraph = new Paragraph("Generado el: " + LocalDateTime.now().format(formatter), FONT_SUBTITLE);
        dateParagraph.setAlignment(Element.ALIGN_CENTER);
        dateParagraph.setSpacingAfter(20);
        document.add(dateParagraph);
    }

    public static PdfPTable createTable(int columns) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        return table;
    }

    public static void addCellHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(COLOR_PRIMARY);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public static void addCellBody(PdfPTable table, String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", FONT_BODY));
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
