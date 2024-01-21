package com.contact.controller;

import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import com.contact.dao.ContactRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.User;
import com.contact.helper.Message;

//import jakarta.persistence.criteria.Path;

import com.contact.entities.Contact;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		//System.out.println("USERNAME " + userName);

		// get the user using usernamne(Email)

		User user = userRepository.findByEmail(userName);
		//System.out.println("USER " + user);
		model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		return "normal/user_dashboard";
	}
	
	
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		User user = new User();
		user.setName("abcd");
		model.addAttribute("user",user);
		return "normal/add_contact_form";
	}
	
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,Model model,@RequestParam("profileImage") MultipartFile file ,Principal principal,HttpSession session) {
		try {
			String name=principal.getName();
			User user=this.userRepository.findByEmail(name);
			
			if (file.isEmpty()) {
				// if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.png");

			} else {
				// file the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");

			}
			
			contact.setUser(user);

			user.getContact().add(contact);
			this.userRepository.save(user);
			model.addAttribute("user",user);
			// message success.......
			session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));

		} catch (Exception e) {
			// TODO: handle exception
		System.out.print("error"+e.getMessage());
		session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));
		}
		return "normal/add_contact_form";

	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");
		// contact ki list ko bhejni hai

		String userName = principal.getName();

		User user = this.userRepository.findByEmail(userName);

		// currentPage-page
		// Contact Per page - 5
		Pageable pageable = PageRequest.of(page, 2);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}


	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepository.findByEmail(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}









}
	
	
