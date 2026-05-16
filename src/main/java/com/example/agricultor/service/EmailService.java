package com.example.agricultor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo electrónico simple de forma asíncrona.
     *
     * @param destinatario Correo que se ingresó en el formulario.
     * @param asunto       El título del correo electrónico.
     * @param mensaje      El cuerpo o contenido del correo.
     */
    @Async // Ejecuta este método en un hilo separado (no bloquea al usuario)
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);

            mailSender.send(email);
            System.out.println("--> Correo enviado con éxito a: " + destinatario);
        } catch (Exception e) {
            // Se captura aquí para que un fallo de red con Gmail jamás afecte la base de datos
            System.err.println("❌ Error crítico al enviar correo a " + destinatario + ": " + e.getMessage());
        }
    }
}