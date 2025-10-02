package com.mycompany.project2.services;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    // ✅ CONFIGURACIÓN SIMPLIFICADA CON GMAIL (MÁS CONFIABLE)
    private String smtpHost = "smtp.gmail.com";
    private String smtpPort = "587";
    private String smtpUsername = "universalmediacolombia@gmail.com"; // Cambiar por tu Gmail
    private String smtpPassword = "koosveagudyaqcnm"; // Contraseña de aplicación
    
    @PostConstruct
    public void init() {
        LOGGER.info("✅ Servicio de correo inicializado con Gmail");
    }
    
    public boolean enviarCorreo(String[] destinatarios, String asunto, String cuerpo) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.ssl.trust", smtpHost);
            
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsername, "Sistema Desayunos"));
            message.setSubject(asunto);
            message.setContent(cuerpo, "text/html; charset=utf-8");
            
            for (String destinatario : destinatarios) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario.trim()));
            }
            
            Transport.send(message);
            LOGGER.log(Level.INFO, "✅ Correo enviado exitosamente a {0} destinatarios", destinatarios.length);
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al enviar correo: " + e.getMessage(), e);
            return false;
        }
    }
}