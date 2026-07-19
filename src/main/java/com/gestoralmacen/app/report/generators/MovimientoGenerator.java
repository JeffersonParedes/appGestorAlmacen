package com.gestoralmacen.app.report.generators;

import com.gestoralmacen.app.entity.HistorialMovimientos;
import com.gestoralmacen.app.report.ReportGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovimientoGenerator {

    public static byte[] generarPDF(List<HistorialMovimientos> movimientos, String nombreEmpresa) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            ReportGenerator.addHeader(document, "Reporte de Movimientos - " + nombreEmpresa);

            PdfPTable table = ReportGenerator.createTable(6);
            table.setWidths(new float[]{15, 25, 12, 12, 16, 20});

            ReportGenerator.addCellHeader(table, "Fecha");
            ReportGenerator.addCellHeader(table, "Producto");
            ReportGenerator.addCellHeader(table, "Tipo");
            ReportGenerator.addCellHeader(table, "Cantidad");
            ReportGenerator.addCellHeader(table, "Almacén");
            ReportGenerator.addCellHeader(table, "Usuario");

            for (HistorialMovimientos mov : movimientos) {
                String fechaStr = mov.getFechaMovimiento() != null ? mov.getFechaMovimiento().format(formatter) : "";
                ReportGenerator.addCellBody(table, fechaStr, Element.ALIGN_CENTER);
                ReportGenerator.addCellBody(table, mov.getProducto().getNombre(), Element.ALIGN_LEFT);
                ReportGenerator.addCellBody(table, mov.getTipoMovimiento(), Element.ALIGN_CENTER);
                ReportGenerator.addCellBody(table, mov.getCantidad().toString(), Element.ALIGN_RIGHT);
                ReportGenerator.addCellBody(table, mov.getAlmacen().getNombre(), Element.ALIGN_LEFT);
                
                String usuarioStr = mov.getUsuario() != null ? mov.getUsuario().getUsuario() : "";
                ReportGenerator.addCellBody(table, usuarioStr, Element.ALIGN_LEFT);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
