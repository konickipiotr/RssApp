package com.app;

import java.io.IOException;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
public class MailSender {
	
	private String email;
	private String efrom = "konicki.piotr@gmail.com";
	
	public MailSender(String email) {
		this.email = email;
	}
	
	public void sendEmail(String message) throws IOException {
		Email from = new Email(efrom);
	    String subject = "Rss messages";
	    Email to = new Email(email);
	    Content content = new Content("text/html", message);
	    Mail mail = new Mail(from, subject, to, content);

	    //SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	    SendGrid sg = new SendGrid("API-KEY");
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	    } catch (IOException ex) {
	      throw ex;
	    }
	}
}

