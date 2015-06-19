package com.fenrir.mailer;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by lars on 6/19/2015.
 */
public class SendVerificationEmail {

    public void sendEmail(String clientUsername, String clientEmail, int token) {

        final String hostUsername = "fenrir.sec@gmail.com";
        final String hostPassword = "f3nr1rv3r1fy";

//        Use Gmail services
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

//        Create session - authorize with service
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(hostUsername, hostPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(hostUsername));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(clientEmail));

            message.setSubject("Verify your authorization - FENRIRsecurity");
            message.setText("Hello " + clientUsername + ",\n\n" +
                    "Is it you logging in to your account? If so, use the following code to complete this process:\n" +
                    token + "\n\n" +
                    "If you did not request this authorization, ignore this email.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
