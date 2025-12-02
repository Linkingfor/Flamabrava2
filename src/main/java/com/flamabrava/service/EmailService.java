package com.flamabrava.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${app.mail.from}")
  private String fromEmail;

  @Value("${app.mail.fromName:Flama Brava}")
  private String fromName;

  /**
   * Envía el mail de verificación de cuenta.
   * @param to     correo destino
   * @param token  token de verificación
   */
  public void sendVerificationEmail(String to, String token) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(
              message,
              false,
              StandardCharsets.UTF_8.name()
      );

      // ✅ CLAVE: SendGrid exige FROM
      helper.setFrom(new InternetAddress(fromEmail, fromName));
      helper.setReplyTo(fromEmail);

      helper.setTo(to);
      helper.setSubject("Verifica tu cuenta en FlamaBrava");

      // Texto plano (como lo tenías)
      helper.setText(
              "Usa este Codigo para Verificar tu Cuenta en la Pagina FlamaBrava:\n" + token,
              false
      );

      mailSender.send(message);
    } catch (Exception e) {
      throw new RuntimeException("Error enviando correo de verificación", e);
    }
  }

  /**
   * Envía la boleta generada como PDF adjunto.
   * @param to     correo destino
   * @param pdf    bytes del PDF
   * @param id     id de la boleta/pedido
   */
  public void sendInvoiceEmail(String to, byte[] pdf, Long id) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    // ✅ también pon FROM aquí por consistencia
    try {
      helper.setFrom(new InternetAddress(fromEmail, fromName));
      helper.setReplyTo(fromEmail);
    } catch (Exception ignored) {}

    helper.setTo(to);
    helper.setSubject("Tu boleta FlamaBrava #" + id);
    helper.setText("<p>Adjunto encontrarás tu boleta en PDF.</p>", true);
    helper.addAttachment("boleta-" + id + ".pdf", new ByteArrayResource(pdf));
    mailSender.send(message);
  }
}
