package com.app;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.db.EmailDAO;
import com.app.db.RssDAO;
import com.app.model.EmailModel;
import com.app.model.Rss;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Controller
public class SendController {
	
	class MsgType{
		public String html_msg ="";
		public String plain_msg ="";
	}
	 
	@Autowired
	private RssDAO rssDAO;
	@Autowired
	private EmailDAO emailDAO;

	@GetMapping("/send")
	public String send(Model model) {
		
		if(AppApplication.isEmail == false) {
			model.addAttribute("message", "Nie został podany email");
			return "redirect:/";
		}
		
		EmailModel em = emailDAO.findById(1l).orElse(null);
		model.addAttribute("email", em.getEmail());
		
		List<Rss> tosend = rssDAO.findAll();
		if(tosend.isEmpty())
			return "redirect:/";
		String email_content = "";
		String textarea_content = "";
		
		String errormessage = "";
		for(Rss r : tosend) {
			try {
				MsgType msg = buildMessage(r.getRsslink());
				email_content += msg.html_msg;
				textarea_content += msg.plain_msg;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				errormessage += "Nie ma takiej strony " + r.getRsslink() + "\n";
			} catch (FeedException e) {
				errormessage += "To nie jest wiadomość rss " + r.getRsslink() + "\n";
			}
		}
		
		if(!email_content.isBlank()) {
			email_content = "<html><body>" +email_content+ "</body></html>";
		
			MailSender m = new MailSender(em.getEmail());
			try {
				m.sendEmail(email_content);
			} catch (IOException e) {
				errormessage += "Nie udało sie wysłać wiadomości ";
			}
		}else {
			errormessage += "Nie udało sie wysłać wiadomości ";
		}
		
		model.addAttribute("message", errormessage);
		model.addAttribute("rss_list", tosend);
		model.addAttribute("textarea_content", textarea_content);
		return "index";
	}
	
	private MsgType buildMessage(String url) throws IOException, IllegalArgumentException, FeedException {

		MsgType msg = new MsgType();
		URL feedSource;

		feedSource = new URL(url);
		SyndFeedInput input = new SyndFeedInput();
		XmlReader reader = new XmlReader(feedSource);
		SyndFeed feed = input.build(reader);
		
		msg.plain_msg += feed.getTitle() + "\n\n";
		
		for (SyndEntry entry : feed.getEntries()) {
		
			msg.plain_msg += entry.getTitle() + "\n";
			msg.plain_msg += entry.getDescription().getValue() + "\n";
			msg.plain_msg += "[" + entry.getPublishedDate() + "]\n";
			msg.plain_msg += entry.getLink() + "\n\n";
			
			msg.html_msg += "<p><b>" + entry.getTitle() + "</b></p>\n";
			msg.html_msg += "<p><b>[" + entry.getPublishedDate() + "]</b></p>\n";
			msg.html_msg += "<p>" + entry.getDescription().getValue() + "</p>\n";
			msg.html_msg += "<a href=\"" + entry.getLink() + "\">" + entry.getLink() + "</a>\n\n";
        }
		return msg;
	}
}
