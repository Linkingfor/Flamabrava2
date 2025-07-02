package com.flamabrava.controller;

import com.flamabrava.model.Pedido;
import com.flamabrava.service.PedidoService;
import com.flamabrava.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = {
    "https://polleriaflamabrava.netlify.app",
    "http://localhost:3000"
})
public class DocumentoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ReporteService reporteService;

    /**
     * GET /api/documentos/boleta/{pedidoId}
     * Genera y descarga la boleta en PDF con PDFBox.
     */
    @GetMapping("/boleta/{pedidoId}")
    public void descargarBoleta(@PathVariable Integer pedidoId,
                                HttpServletResponse response) throws IOException {

                                    
        // 1️⃣ Recuperar el pedido
        Optional<Pedido> opt = pedidoService.findByIdWithDetalle(pedidoId);
        Pedido pedido = opt.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado")
        );
                
String nombre   = pedido.getCliente().getNombre() != null ? pedido.getCliente().getNombre() : "";
String apellido = pedido.getCliente().getApellido() != null ? pedido.getCliente().getApellido() : "";
String dni      = pedido.getCliente().getDni() != null ? pedido.getCliente().getDni() : "";
String correo   = pedido.getCliente().getEmail() != null ? pedido.getCliente().getEmail() : "";
String telefono = pedido.getCliente().getTelefono() != null ? pedido.getCliente().getTelefono() : "";

String nombreCompleto = (nombre + " " + apellido).trim();

// Limitar longitud y limpiar caracteres
nombreCompleto = nombreCompleto.length() > 30 ? nombreCompleto.substring(0, 30) : nombreCompleto;
dni = dni.length() > 30 ? dni.substring(0, 30) : dni;
correo = correo.length() > 30 ? correo.substring(0, 30) : correo;
telefono = telefono.length() > 30 ? telefono.substring(0, 30) : telefono;

nombreCompleto = nombreCompleto.replaceAll("[^\\x00-\\x7F]", "");
dni = dni.replaceAll("[^\\x00-\\x7F]", "");
correo = correo.replaceAll("[^\\x00-\\x7F]", "");
telefono = telefono.replaceAll("[^\\x00-\\x7F]", "");
        // 2️⃣ Configurar response
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=boleta_" + pedidoId + ".pdf");

        // 3️⃣ Generar PDF con PDFBox
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(226, 600)); // tamaño ticket
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 10;
                float y = page.getMediaBox().getHeight() - margin;

                // Título
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("FLAMABRAVA S.A.C.");
                cs.endText();

                y -= 14;
                cs.beginText();
                cs.setFont(PDType1Font.COURIER, 8);
                cs.newLineAtOffset(margin, y);
                cs.showText("RUC: 20512345678");
                cs.endText();

                y -= 20;
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 10);
                cs.newLineAtOffset(margin, y);
                cs.showText("BOLETA DE VENTA ELECTRÓNICA");
                cs.endText();

                y -= 14;
                String serie = "BE01";
                String numero = String.format("%06d", pedido.getId());
                cs.beginText();
                cs.setFont(PDType1Font.COURIER, 8);
                cs.newLineAtOffset(margin, y);
                cs.showText("Serie: " + serie + "  N° " + numero);
                cs.endText();

                // Datos pedido
                y -= 18;
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Fecha: " + fmt.format(pedido.getFechaPedido()));
                cs.endText();

                y -= 14;
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Cliente ID: " + pedido.getCliente().getId());
                cs.endText();


// Imprimir campos
y -= 18;
cs.beginText();
cs.newLineAtOffset(margin, y);
cs.showText("Cliente : " + nombreCompleto);
cs.endText();

y -= 12;
cs.beginText();
cs.newLineAtOffset(margin, y);
cs.showText("DNI     : " + dni);
cs.endText();

y -= 12;
cs.beginText();
cs.newLineAtOffset(margin, y);
cs.showText("Correo  : " + correo);
cs.endText();

y -= 12;
cs.beginText();
cs.newLineAtOffset(margin, y);
cs.showText("Teléf.  : " + telefono);
cs.endText();

y -= 14;
cs.beginText();
cs.newLineAtOffset(margin, y);
cs.showText("---------------------------");
cs.endText();

                // Cabecera tabla
                y -= 20;
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("Cant  Descripción         Subtotal");
                cs.endText();

                // Líneas
                String detalles = pedido.getDetalles();
                if (detalles != null && !detalles.isBlank()) {
                    String[] items = detalles.split("\\s*,\\s*");
                    for (String it : items) {
                        y -= 12;
                        cs.beginText();
                        cs.newLineAtOffset(margin, y);
                        cs.showText(formatLinea(it));
                        cs.endText();
                    }
                } else {
                    y -= 12;
                    cs.beginText();
                    cs.newLineAtOffset(margin, y);
                    cs.showText("No hay productos.");
                    cs.endText();
                }

                // Total
                y -= 20;
                cs.beginText();
                cs.setFont(PDType1Font.COURIER_BOLD, 10);
                cs.newLineAtOffset(margin, y);
                cs.showText("TOTAL: S/ " + pedido.getTotal().setScale(2));
                cs.endText();
            }
try {
    // El bloque que genera el PDF...
} catch (Exception e) {
    e.printStackTrace(); // Te mostrará en consola qué lo causa realmente
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar boleta");
}
            // 4️⃣ Enviar el PDF
            doc.save(response.getOutputStream());
        }
    }

    /**
     * GET /api/documentos/reporte
     * Descarga el reporte completo en PDF.
     */
    @GetMapping("/reporte")
    public ResponseEntity<byte[]> descargarReporte() throws Exception {
        byte[] pdf = reporteService.generarReporteCompleto();
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"reporte_reservas_ventas.pdf\"")
            .body(pdf);
    }

    /** Ajusta el formato de cada línea según longitud deseada */
    private String formatLinea(String item) {
        // Espera un string como "2x Pollo" o "1x Arroz"
        String[] partes = item.split("x\\s*", 2);
        if (partes.length==2) {
            String cant = partes[0]+"x";
            String desc = partes[1];
            if (desc.length()>15) desc = desc.substring(0,12)+"...";
            return String.format("%-4s %-15s", cant, desc);
        }
        return item;
    }
}
