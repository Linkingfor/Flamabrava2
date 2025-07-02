package com.flamabrava.service;

import com.flamabrava.model.Reserva;
import com.flamabrava.model.Pedido;
import com.flamabrava.repository.ReservaRepository;
import com.flamabrava.repository.PedidoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service
public class ReporteService {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    public byte[] generarReporteCompleto() throws Exception {

        Iterable<Reserva> reservas = reservaRepo.findAll();
        Iterable<Pedido>  pedidos  = pedidoRepo.findAll();

        // 1️⃣ Documento A4 apaisado
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // 2️⃣ Fuentes
        Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        /* =================================================
         *               REPORTE DE RESERVAS
         * ================================================= */
        document.add(new Paragraph("REPORTE DE RESERVAS", fTitulo));
        document.add(Chunk.NEWLINE);

        // Ahora la tabla tiene 7 columnas
        PdfPTable t1 = new PdfPTable(8);
        t1.setWidthPercentage(100);

        // Encabezados
        Stream.of("ID", "Cliente", "DNI", "Teléfono", "Correo",
                  "Mesa", "Reserva Para", "Fecha")
              .forEach(h -> {
                  PdfPCell cell = new PdfPCell(new Phrase(h, fNormal));
                  cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                  cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                  t1.addCell(cell);
              });

        // Filas
        for (Reserva r : reservas) {
            t1.addCell(r.getId().toString());
            t1.addCell(r.getCliente().getNombre());
            t1.addCell(r.getCliente().getDni());   
            t1.addCell(r.getCliente().getTelefono());
            t1.addCell(r.getCliente().getEmail());
            t1.addCell(String.valueOf(r.getMesa().getNumero()));
            t1.addCell(String.valueOf(r.getNumPersonas()));           // ← NUEVO
            t1.addCell(r.getFecha().toString());
        }
        document.add(t1);

        document.add(Chunk.NEWLINE);

        /* =================================================
         *               REPORTE DE VENTAS
         * ================================================= */
        document.add(new Paragraph("REPORTE DE VENTAS", fTitulo));
        document.add(Chunk.NEWLINE);

        PdfPTable t2 = new PdfPTable(6);
        t2.setWidthPercentage(100);

        Stream.of("ID", "Cliente", "Total", "Fecha", "Estado", "Detalle")
              .forEach(h -> {
                  PdfPCell cell = new PdfPCell(new Phrase(h, fNormal));
                  cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                  cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                  t2.addCell(cell);
              });

        for (Pedido p : pedidos) {
            t2.addCell(p.getId().toString());
            t2.addCell(p.getCliente().getNombre());
            t2.addCell("S/ " + p.getTotal().setScale(2));
            t2.addCell(p.getFechaPedido().toString());
            t2.addCell(p.getEstado());

            String detalles = (p.getDetalles() == null || p.getDetalles().isBlank())
                                ? "-" : p.getDetalles();
            PdfPCell detalleCell = new PdfPCell(new Phrase(detalles, fNormal));
            detalleCell.setNoWrap(false);
            t2.addCell(detalleCell);
        }
        document.add(t2);

        // 3️⃣ Cerrar documento y devolver bytes
        document.close();
        return baos.toByteArray();
    }
}
