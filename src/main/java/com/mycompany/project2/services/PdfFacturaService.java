package com.mycompany.project2.services;

import com.mycompany.project2.entities.Factura;
import com.mycompany.project2.entities.FacturaDetalle;
import com.mycompany.project2.entities.Producto;
import com.mycompany.project2.entities.Usuario;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.Stateless;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Stateless
public class PdfFacturaService {

    public byte[] generarFactura(Factura factura, List<FacturaDetalle> detalles) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Encabezado
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("FACTURA - Desayunos y Detalles", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(Chunk.NEWLINE);

        // Info factura
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = factura.getFechaFactura() != null ? sdf.format(factura.getFechaFactura()) : "";

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidths(new float[]{70, 30});
        infoTable.setWidthPercentage(100f);

        infoTable.addCell(createCell("Factura #: " + factura.getIdFactura(), false));
        infoTable.addCell(createCell("Fecha: " + fecha, false));

        // Cliente (si existe)
        if (factura.getUsuarioIDUSUARIOCLIENTE() != null) {
            Usuario cli = factura.getUsuarioIDUSUARIOCLIENTE();
            infoTable.addCell(createCell("Cliente: " + (cli.getNombreUsuario() != null ? cli.getNombreUsuario() : cli.getCorreoUsuario()), false));
            infoTable.addCell(createCell("Método pago: " + (factura.getMetodoPago() == null ? "-" : factura.getMetodoPago()), false));
        } else {
            infoTable.addCell(createCell("Cliente: --", false));
            infoTable.addCell(createCell("Método pago: " + (factura.getMetodoPago() == null ? "-" : factura.getMetodoPago()), false));
        }

        infoTable.addCell(createCell("Estado: " + (factura.getEstadoFactura() == null ? "-" : factura.getEstadoFactura()), false));
        infoTable.addCell(createCell("Total: $" + (factura.getTotalFactura() != null ? factura.getTotalFactura().toString() : "0.00"), false));

        doc.add(infoTable);
        doc.add(Chunk.NEWLINE);

        // Tabla items
        PdfPTable table = new PdfPTable(new float[]{50, 10, 20, 20});
        table.setWidthPercentage(100f);
        table.addCell(createHeaderCell("Producto"));
        table.addCell(createHeaderCell("Cant"));
        table.addCell(createHeaderCell("Precio unit."));
        table.addCell(createHeaderCell("Subtotal"));

        for (FacturaDetalle d : detalles) {
            Producto p = d.getProductoIDPRODUCTO();
            String nombre = p != null ? p.getNombreProducto() : "Producto " + (d.getProductoIDPRODUCTO() != null ? d.getProductoIDPRODUCTO().getIdProducto() : "");
            table.addCell(createCell(nombre, false));
            table.addCell(createCell(String.valueOf(d.getCantidad()), false));
            table.addCell(createCell(d.getPrecioUnitario().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), false));
            table.addCell(createCell(d.getSubtotal().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), false));
        }

        doc.add(table);
        doc.add(Chunk.NEWLINE);

        Paragraph total = new Paragraph("TOTAL: $" + (factura.getTotalFactura() != null ? factura.getTotalFactura().setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "0.00"),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        total.setAlignment(Element.ALIGN_RIGHT);
        doc.add(total);

        doc.close();
        return baos.toByteArray();
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c.setPadding(6);
        return c;
    }

    private PdfPCell createCell(String text, boolean bold) {
        Font f = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9) : FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, f));
        c.setPadding(6);
        return c;
    }
}
