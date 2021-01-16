package pl.dernovyi.coushgameback.service.impl;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static pl.dernovyi.coushgameback.constant.EmailConstant.*;

@Service
public class EmailService {
    @Value(value = "${USERNAME_MAIL}")
    private static String username_mail;
    @Value(value = "${PASSWORD_MAIL}")
    private static String password_mail;

    public void sendNewPasswordEmail(String username, String password, String email) throws MessagingException {
        Message message = createEmail(username,password,email);
        SMTPTransport smtpTransport =(SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, username_mail, password_mail);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();

    }

    private Message createEmail(String username, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.addRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + username + ", \n \n Your new account password is: "+ password + "\n \n The Support Team" );
        message.setSentDate(new Date());
        message.saveChanges();
        return message;


    }
    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }
}
