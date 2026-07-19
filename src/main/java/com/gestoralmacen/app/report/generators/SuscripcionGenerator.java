package com.gestoralmacen.app.report.generators;

import com.gestoralmacen.app.entity.Suscripcion;
import com.gestoralmacen.app.report.ReportGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SuscripcionGenerator {

    public static byte[] generarPDF(List<Suscripcion> suscripciones) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            ReportGenerator.addHeader(document, "Reporte Global de Suscripciones");

            PdfPTable table = ReportGenerator.createTable(6);
            table.setWidths(new float[]{25, 15, 15, 15, 15, 15});

            ReportGenerator.addCellHeader(table, "Empresa");
            ReportGenerator.addCellHeader(table, "Plan");
            ReportGenerator.addCellHeader(table, "Inicio");
            ReportGenerator.addCellHeader(table, "Fin");
            ReportGenerator.addCellHeader(table, "Monto");
            ReportGenerator.addCellHeader(table, "Estado");

            for (Suscripcion sub : suscripciones) {
                String empresaName = sub.getEmpresa() != null ? sub.getEmpresa().getRazonSocial() : "N/A";
                ReportGenerator.addCellBody(table, empresaName, Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, "Plan SaaS", Element.ALIGN_CENTER);
                
                String inicioStr = sub.getFechaInicio() != null ? sub.getFechaInicio().format(formatter) : "";
                ReportGenerator.addCellBody(table, inicioStr, Element.ALIGN_CENTER);
                
                String finStr = sub.getFechaFin() != null ? sub.getFechaFin().format(formatter) : "";
                ReportGenerator.addCellBody(table, finStr, Element.ALIGN_CENTER);
                
                String montoStr = sub.getMontoPagado() != null ? String.format("%.2f", sub.getMontoPagado()) : "0.00";
                ReportGenerator.addCellBody(table, montoStr, Element.ALIGN_RIGHT);
                ReportGenerator.addCellBody(table, sub.getEstadoPago(), Element.ALIGN_CENTER);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
