package com.bistro.notifications.service;

import com.bistro.shared.NonRetryableException;
import jakarta.mail.SendFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    private static final String REJECTED_ADDRESS = "rechazado@bistro.test";

    public void notifyConfirmed(String to, String reservationCode, String tableNumber){

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Tu reserva " + reservationCode + " está confirmada");
        msg.setText("Te asignamos la mesa " + tableNumber + ". ¡Te esperamos!");

        try {
            sendEmail(msg);
            log.info("Aviso de CONFIRMACIÓN enviado a {} (reserva {})", to, reservationCode);
        } catch (MailSendException e) {

            boolean permanentlyRejected = Arrays.stream(e.getMessageExceptions())
                    .anyMatch(ex -> ex instanceof SendFailedException);

            if(permanentlyRejected){
                log.warn("Dirección rechazada de forma definitiva: {}. No se reintenta.", to);

                throw new NonRetryableException(
                        "El servidor rechazó la dirección de forma definitiva: " + to, e);
            }
            throw e;
        }
    }

    public void notifyRejected(String to, String reservationCode, String reason){

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Tu reserva " + reservationCode + " no pudo confirmarse");
        msg.setText("Motivo: " + reason);

        mailSender.send(msg);

        log.info("Aviso de RECHAZO enviado a {} (reserva {})", to, reservationCode);
    }

    public void notifyCancelled(String to, String reservationCode){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Tu reserva " + reservationCode + " fue cancelada");
        msg.setText("Confirmamos la cancelación de tu reserva. ¡Esperamos verte pronto!");

        mailSender.send(msg);

        log.info("Aviso de CANCELACIÓN enviado a {} (reserva {})", to, reservationCode);
    }

    private void sendEmail(SimpleMailMessage msg){
        String[] recipients = msg.getTo();

        if(recipients!=null && recipients.length > 0 && REJECTED_ADDRESS.equals(recipients[0])){
            SendFailedException rejection = new SendFailedException("La dirección no existe: " + recipients[0]);

            throw new MailSendException(Map.of((Object) msg, (Exception) rejection));
        }

        mailSender.send(msg);
    }

}















