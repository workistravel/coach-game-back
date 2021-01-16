package pl.dernovyi.coushgameback.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;

import static pl.dernovyi.coushgameback.constant.EmailConstant.*;
@Service
public class EmailGridService {
    @Value(value = "${SENDGRID_API_KEY}")
    private String SENDGRID_API_KEY;
    private Logger LOGGER = LoggerFactory.getLogger(EmailGridService.class);
    public void sendNewPasswordEmail(String username, String password, String emailTo) throws  IOException {
        Response response = createResponce(username, password, emailTo);

        LOGGER.info(String.valueOf(response.getStatusCode()));
        LOGGER.info(String.valueOf(response.getHeaders()));
        LOGGER.info(String.valueOf(response.getBody()));

    }

    private Response createResponce(String username, String password, String emailTo) throws IOException {
        Email from = new Email("game4coach@ukr.net");
        Email to = new Email(emailTo);
        String subject = EMAIL_SUBJECT;

        Content content = new Content("text/plain", "Hello " + username + ", \n \n Your new account password is: "+ password + "\n \n The Support Team");

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        return response;
    }

}
