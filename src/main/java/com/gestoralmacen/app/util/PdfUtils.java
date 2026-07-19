package com.gestoralmacen.app.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.gestoralmacen.app.report.ReportGenerator;

public final class PdfUtils {

    private PdfUtils() {
    }

    public static void setStandardPageMargins(Document document) {
        document.setMargins(36, 36, 54, 36);
    }

    public static void addSectionSpacing(Document document, int points) throws DocumentException {
        Paragraph spacing = new Paragraph(" ");
        spacing.setSpacingBefore(points);
        document.add(spacing);
    }
}
