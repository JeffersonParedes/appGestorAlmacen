package com.gestoralmacen.app.report.generators;

import com.gestoralmacen.app.entity.Inventario;
import com.gestoralmacen.app.report.ReportGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class InventarioGenerator {

    public static byte[] generarPDF(List<Inventario> inventarios, String nombreEmpresa) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            ReportGenerator.addHeader(document, "Reporte de Inventario - " + nombreEmpresa);

            PdfPTable table = ReportGenerator.createTable(5);
            table.setWidths(new float[]{30, 20, 15, 15, 20});

            ReportGenerator.addCellHeader(table, "Producto");
            ReportGenerator.addCellHeader(table, "Almacén");
            ReportGenerator.addCellHeader(table, "Stock Actual");
            ReportGenerator.addCellHeader(table, "Stock Mínimo");
            ReportGenerator.addCellHeader(table, "Estado");

            for (Inventario inv : inventarios) {
                ReportGenerator.addCellBody(table, inv.getProducto().getNombre(), Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, inv.getAlmacen().getNombre(), Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, inv.getStockActual().toString(), Element.ALIGN_RIGHT);
                ReportGenerator.addCellBody(table, inv.getStockMinimo().toString(), Element.ALIGN_RIGHT);

                String estado = (inv.getStockActual().compareTo(inv.getStockMinimo()) <= 0) ? "STOCK MÍNIMO" : "ÓPTIMO";
                ReportGenerator.addCellBody(table, estado, Element.ALIGN_CENTER);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
