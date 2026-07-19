package com.gestoralmacen.app.report.generators;

import com.gestoralmacen.app.entity.Producto;
import com.gestoralmacen.app.report.ReportGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProductoGenerator {

    public static byte[] generarPDF(List<Producto> productos, String nombreEmpresa) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            ReportGenerator.addHeader(document, "Catálogo de Productos - " + nombreEmpresa);

            PdfPTable table = ReportGenerator.createTable(5);
            table.setWidths(new float[]{15, 30, 25, 15, 15});

            ReportGenerator.addCellHeader(table, "Código");
            ReportGenerator.addCellHeader(table, "Nombre");
            ReportGenerator.addCellHeader(table, "Categoría");
            ReportGenerator.addCellHeader(table, "Precio Ref.");
            ReportGenerator.addCellHeader(table, "Estado");

            for (Producto prod : productos) {
                ReportGenerator.addCellBody(table, prod.getCodigoBarras(), Element.ALIGN_CENTER);
                ReportGenerator.addCellBody(table, prod.getNombre(), Element.ALIGN_LEFT);
                
                String catStr = prod.getCategoria() != null ? prod.getCategoria().getNombre() : "";
                ReportGenerator.addCellBody(table, catStr, Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, prod.getPrecio() != null ? String.format("%.2f", prod.getPrecio()) : "0.00", Element.ALIGN_RIGHT);
                ReportGenerator.addCellBody(table, prod.getEstado(), Element.ALIGN_CENTER);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
