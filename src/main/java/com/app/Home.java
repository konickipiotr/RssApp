package com.app;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.db.EmailDAO;
import com.app.db.RssDAO;
import com.app.model.EmailModel;
import com.app.model.Rss;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class Home {

	@Autowired
	private EmailDAO emailDAO;
	@Autowired
	private RssDAO rssDAO;

	@GetMapping("/")
	public String home(Model model, String message) {
		List<Rss> rss_list = rssDAO.findAll();

		if (AppApplication.isEmail == true) {
			String em = emailDAO.findAll().get(0).getEmail();
			model.addAttribute("email", em);
			model.addAttribute("rss_list", rss_list);
		}

		model.addAttribute("message", message);
		return "index";
	}

	@PostMapping("/")
	public String save(@RequestParam("email") String semail, @RequestParam("rsslink") String srsslink, RedirectAttributes ra) {

		EmailModel email = new EmailModel(semail);
		Rss rss = new Rss(srsslink);


		if(AppApplication.isEmail == false) {
			if(email.getEmail() == null || email.getEmail().isBlank()) {
				ra.addAttribute("message", "Pole email nie może byc puste");
				return "redirect:/";
			}
			emailDAO.save(email);
			AppApplication.isEmail = true;
		}
		
		if(rss.getRsslink() == null || rss.getRsslink().isBlank()) {
			ra.addAttribute("message", "Pole rss nie może byc puste");
			return "redirect:/";
		}
		
		rssDAO.save(rss);
		
		ra.addAttribute("message", "success");
		System.out.println("jestem w post2");
		return "redirect:/";
	}

	@PostMapping("/delete")
	public String deltete(Long deleteid[]) {
		if (deleteid == null)
			return "redirect:/";

		for (Long l : deleteid) {
			rssDAO.deleteById(l);
		}
		return "redirect:/";
	}

	@GetMapping("/delete/{id}")
	public String deltete(@PathVariable("id") Long id) {
		rssDAO.deleteById(id);
		return "redirect:/";
	}

	@GetMapping("/clear")
	public String deltete() {
		AppApplication.isEmail = false;
		rssDAO.deleteAll();
		emailDAO.deleteAll();
		return "index";
	}
}
