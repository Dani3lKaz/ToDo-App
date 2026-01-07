package ToDoApp.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSender implements Runnable{
    private static final Logger logger = Logger.getLogger(EmailSender.class.getName());
    private final String fromAdress = "danielkaz2005@gmail.com";
    private final String password = "qoqm cnrg tmbs tqnm";
    private final String toAdress;
    private final String username;
    private final LocalDate date;

    public EmailSender(String toAdress, String username, LocalDate date) {
        this.toAdress = toAdress;
        this.username = username;
        this.date = date;
    }

    @Override
    public void run() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAdress, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAdress));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAdress));
            msg.setSubject("New task is waiting");
            msg.setText("New task assigned to " + username + ".\nDeadline: " + date);
            Transport.send(msg);
            logger.log(Level.INFO, "Email sent successfully!");
        }catch (MessagingException mex) {
            logger.log(Level.WARNING, "Failed to sent email!");
        }
    }
}
