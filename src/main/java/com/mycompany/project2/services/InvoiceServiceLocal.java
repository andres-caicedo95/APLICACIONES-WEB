package com.mycompany.project2.services;

public interface InvoiceServiceLocal {
    byte[] generarPdfFactura(Long idPedido) throws Exception;
    String guardarPdfEnServidor(byte[] pdfBytes, Long idPedido) throws Exception;
}
