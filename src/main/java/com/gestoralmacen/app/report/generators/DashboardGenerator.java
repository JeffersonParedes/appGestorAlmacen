package com.gestoralmacen.app.report.generators;

import com.gestoralmacen.app.report.ReportGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class DashboardGenerator {

    public static byte[] generarPDF(String titulo, Map<String, String> metrics) {
        Document document = new Document(PageSize.A4, 54, 54, 54, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            ReportGenerator.addHeader(document, titulo);

            Paragraph intro = new Paragraph("Resumen ejecutivo de indicadores clave del Dashboard:", ReportGenerator.FONT_BODY);
            intro.setSpacingAfter(15);
            document.add(intro);

            PdfPTable table = ReportGenerator.createTable(2);
            table.setWidths(new float[]{60, 40});

            ReportGenerator.addCellHeader(table, "Indicador / Métrica");
            ReportGenerator.addCellHeader(table, "Valor");

            for (Map.Entry<String, String> entry : metrics.entrySet()) {
                ReportGenerator.addCellBody(table, entry.getKey(), Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, entry.getValue(), Element.ALIGN_CENTER);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
