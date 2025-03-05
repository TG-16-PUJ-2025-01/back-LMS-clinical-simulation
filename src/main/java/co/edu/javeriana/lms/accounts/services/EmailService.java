package co.edu.javeriana.lms.accounts.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Método para enviar correos electrónicos
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Correo enviado a: {}", to);
        } catch (Exception e) {
            log.error("Error enviando correo a {}: {}", to, e.getMessage());
        }
    }

    // Método para actualizar solo el host, username y password
    public void updateMailConfig(String host, String username, String password) {
        // Actualizar las propiedades del JavaMailSender
        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
        mailSenderImpl.setHost(host);
        mailSenderImpl.setUsername(username);
        mailSenderImpl.setPassword(password);

        // Guardar la configuración en el archivo .env
        saveConfigToEnv(host, username, password);

        log.info("Configuración del correo actualizada: host={}, username={}", host, username);
    }

    // Método para guardar la configuración en el archivo .env
    private void saveConfigToEnv(String host, String username, String password) {
        String envFilePath = Paths.get(".env").toAbsolutePath().toString(); // Ruta absoluta del archivo .env
        List<String> newLines = new ArrayList<>(); // Lista para almacenar las líneas actualizadas

        try (BufferedReader reader = new BufferedReader(new FileReader(envFilePath))) {
            String line;
            // Leer el archivo línea por línea
            while ((line = reader.readLine()) != null) {
                // Actualizar las líneas necesarias
                if (line.startsWith("SMTP_HOST=")) {
                    newLines.add("SMTP_HOST=" + host);
                } else if (line.startsWith("MAIL_USERNAME=")) {
                    newLines.add("MAIL_USERNAME=" + username);
                } else if (line.startsWith("MAIL_PASSWORD=")) {
                    newLines.add("MAIL_PASSWORD=" + password);
                } else {
                    newLines.add(line); // Mantener las demás líneas sin cambios
                }
            }
        } catch (IOException e) {
            log.error("Error leyendo el archivo .env: {}", e.getMessage());
            return;
        }

        // Reescribir el archivo .env con las líneas actualizadas
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(envFilePath))) {
            for (String line : newLines) {
                writer.write(line);
                writer.newLine();
            }
            log.info("Configuración guardada en el archivo .env: {}", envFilePath);
        } catch (IOException e) {
            log.error("Error guardando la configuración en el archivo .env: {}", e.getMessage());
        }
    }
}