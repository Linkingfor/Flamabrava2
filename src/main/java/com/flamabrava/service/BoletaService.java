package com.flamabrava.service;

import com.flamabrava.model.Pedido;
import com.flamabrava.model.DetallePedido;
import com.flamabrava.repository.PedidoRepository;
import com.flamabrava.repository.DetallePedidoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class BoletaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detalleRepo;

    public byte[] generarBoleta(Long pedidoId) throws Exception {

        Integer id = pedidoId.intValue();

        // 1) Recuperar pedido y detalle
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));
        List<DetallePedido> detalles = detalleRepo.findByPedido_Id(id);

        // 2) PDF — aumentar alto porque 600 px se queda corto
        Rectangle pageSize = new Rectangle(226, 1000);
        Document document = new Document(pageSize, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // 3) Fuentes compatibles UTF-8
        Font bold10   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font normal9  = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        Font small8   = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

        // 4) Cabecera
        Paragraph empresa = new Paragraph("FLAMABRAVA S.A.C.\nRUC: 20512345678", bold10);
        empresa.setAlignment(Element.ALIGN_CENTER);
        document.add(empresa);

        document.add(new Paragraph("Pollería y Parrilla", small8));
        document.add(new Paragraph("Av. Las Brasas 123 - Lima, Perú", small8));
        document.add(Chunk.NEWLINE);

        // 5) Boleta
        document.add(new Paragraph("BOLETA DE VENTA ELECTRÓNICA", bold10));
        String serie = "BE01";
        String numero = String.format("%06d", pedido.getId());
        document.add(new Paragraph("Serie: " + serie + "   N° " + numero, normal9));
        document.add(new Paragraph("────────────────────────", small8));

        // 6) Fecha y cliente
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        document.add(new Paragraph("Cliente ID: " + pedido.getCliente().getId(), normal9));
        document.add(new Paragraph("Fecha: " + fmt.format(pedido.getFechaPedido()), normal9));
        document.add(new Paragraph("────────────────────────", small8));

        // 7) Tabla de artículos
        PdfPTable tabla = new PdfPTable(new float[]{2f, 1f, 1f});
        tabla.setWidthPercentage(100);
        tabla.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        tabla.setSpacingBefore(5);

        PdfPCell h1 = new PdfPCell(new Phrase("Descripción", normal9));
        PdfPCell h2 = new PdfPCell(new Phrase("Cant.", normal9));
        PdfPCell h3 = new PdfPCell(new Phrase("Subtotal", normal9));

        for (PdfPCell c : List.of(h1, h2, h3)) {
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c.setPadding(4);
            tabla.addCell(c);
        }

        for (DetallePedido d : detalles) {
            tabla.addCell(new Phrase(d.getProducto().getNombre(), normal9));

            PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), normal9));
            cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cantCell);

            PdfPCell subCell = new PdfPCell(new Phrase("S/ " + d.getTotal().setScale(2), normal9));
            subCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(subCell);
        }

        document.add(tabla);

        // 8) Total
        document.add(Chunk.NEWLINE);
        Paragraph total = new Paragraph("TOTAL: S/ " + pedido.getTotal().setScale(2), bold10);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
        return baos.toByteArray();
    }
}
