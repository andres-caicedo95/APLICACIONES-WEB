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
    
    // **NOTA:** La contraseña debe ser una "Contraseña de aplicación" de Google.
    private String smtpHost = "smtp.gmail.com";
    private String smtpPort = "465"; // Puerto 465 para SSL directo (SMTPS)
    private String smtpUsername = "universalmediacolombia@gmail.com";
    private String smtpPassword = "koosveagudyaqcnm";
    
    @PostConstruct
    public void init() {
        LOGGER.info("✅ Servicio de correo inicializado con Gmail y SMTPS (Puerto 465)");
    }
    
    public boolean enviarCorreo(String[] destinatarios, String asunto, String cuerpo) {
        try {
            Properties props = new Properties();
            
            // 1. Configuración básica de conexión y autenticación
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            
            // 2. Configuración CLAVE para forzar SMTPS y evitar el conflicto de SSLSocketFactory
            // Esto le indica a JavaMail que utilice el protocolo SMTPS (Secure SMTP)
            props.put("mail.transport.protocol", "smtps");
            
            // 3. Habilitar SSL directamente y confiar en el host (importante para self-signed/proxy)
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);
            
            // Opcional: Para el conflicto específico de NoSuchMethodError, forzar el uso de TLS 1.2 o 1.3
            // props.put("mail.smtp.ssl.protocols", "TLSv1.2"); 
            
            // Añadir propiedades de debug y timeout para mejor diagnóstico
            props.put("mail.debug", "true"); 
            props.put("mail.smtp.connectiontimeout", 10000); // 10 segundos
            props.put("mail.smtp.timeout", 10000); 
            
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
            
            // Cuando el protocolo es SMTPS, JavaMail utilizará el proveedor SMTPS
            // El GlassFish EJB invoca Transport.send(message);
            Transport transport = session.getTransport("smtps");
            try {
                transport.connect(smtpHost, smtpUsername, smtpPassword);
                transport.sendMessage(message, message.getAllRecipients());
            } finally {
                transport.close();
            }
            
            LOGGER.log(Level.INFO, "Correo enviado exitosamente a {0} destinatarios", destinatarios.length);
            return true;
            
        } catch (MessagingException e) {
            // Este catch maneja errores de autenticación, conexión y envío.
            LOGGER.log(Level.SEVERE, "Error de mensajería (MessagingException). Revise credenciales, puerto y conexión: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // Este catch maneja el NoSuchMethodError u otros errores inesperados.
            LOGGER.log(Level.SEVERE, "Error inesperado al enviar correo. Conflicto de JARs o JRE: " + e.getMessage(), e);
            return false;
        }
    }
}