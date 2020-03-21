package com.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.db.EmailDAO;
import com.app.db.RssDAO;
import com.app.model.EmailModel;
import com.app.model.Rss;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.helpers.mail.objects.Email;

import static org.hamcrest.Matchers.*;

@SpringBootTest
class HomeTest {

	private MockMvc mockMvc;
	@InjectMocks
	private Home home;
	@Mock
	private Model model;
	@Mock
	private RedirectAttributes ra; 
	private ObjectMapper om = new ObjectMapper();
	
	@Mock
	private RssDAO rssDAO;
	@Mock
	private EmailDAO emailDAO;
	
	
	@BeforeEach
	public void setUp() throws Exception{
		mockMvc = MockMvcBuilders.standaloneSetup(home).build();
	}
	
	@Test 
	public void homeControllerReturnOkStatus() throws Exception {
		
		mockMvc.perform(get("/"))
		.andExpect(status().isOk());
	}
	
	@Test
	public void whenAppStartsFirstTimeisEmailShouldBeFalse() {
		assertFalse(AppApplication.isEmail);
	}
	

	@Test
	public void ifEmailIsNullOrEmptyShouldReturnErrorMessage() throws Exception {
		
		EmailModel email = new EmailModel();
		Rss rss = new Rss();

		mockMvc.perform(post("/").requestAttr("email", email).requestAttr("rss", rss).requestAttr("ra", ra))			
		.andExpect(status().is3xxRedirection());
	}
	
}
