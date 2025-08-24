package com.contactapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contactapp.dto.ContactDto;
import com.contactapp.entity.Contact;
import com.contactapp.repository.UserRepository;
import com.contactapp.service.ContactService;
import com.contactapp.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

	@Autowired
	private ContactService contactService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	// Get all contacts for authenticated user
	@GetMapping
	public ResponseEntity<List<Contact>> getAllContacts(HttpServletRequest request) {
		try {
			Long userId = getUserIdFromToken(request);
			List<Contact> contacts = contactService.getAllContacts(userId);
			return ResponseEntity.ok(contacts);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Get single contact by ID
	@GetMapping("/{id}")
	public ResponseEntity<Contact> getContactById(@PathVariable Long id, HttpServletRequest request) {
		try {
			Long userId = getUserIdFromToken(request);
			Contact contact = contactService.getContactById(id, userId);
			return ResponseEntity.ok(contact);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Create new contact
	@PostMapping
	public ResponseEntity<?> createContact(@RequestParam("name") String name, @RequestParam("phoneNo") String phoneNo,
			@RequestParam("email") String email, @RequestParam("gender") String gender,
			@RequestParam(value = "photo", required = false) MultipartFile photo, HttpServletRequest request) {

		try {
			Long userId = getUserIdFromToken(request);

			ContactDto contactDto = new ContactDto();
			contactDto.setName(name);
			contactDto.setPhoneNo(phoneNo);
			contactDto.setEmail(email);
			contactDto.setGender(gender);

			Contact contact = contactService.createContact(contactDto, userId, photo);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Contact created successfully");
			response.put("contact", contact);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	// Update existing contact
	@PutMapping("/{id}")
	public ResponseEntity<?> updateContact(@PathVariable Long id, @RequestParam("name") String name,
			@RequestParam("phoneNo") String phoneNo, @RequestParam("email") String email,
			@RequestParam("gender") String gender, @RequestParam(value = "photo", required = false) MultipartFile photo,
			HttpServletRequest request) {

		try {
			Long userId = getUserIdFromToken(request);

			ContactDto contactDto = new ContactDto();
			contactDto.setName(name);
			contactDto.setPhoneNo(phoneNo);
			contactDto.setEmail(email);
			contactDto.setGender(gender);

			Contact contact = contactService.updateContact(id, contactDto, userId, photo);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Contact updated successfully");
			response.put("contact", contact);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	// Delete contact
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteContact(@PathVariable Long id, HttpServletRequest request) {
		try {
			Long userId = getUserIdFromToken(request);
			contactService.deleteContact(id, userId);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Contact deleted successfully");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	// Search contacts
	@GetMapping("/search")
	public ResponseEntity<List<Contact>> searchContacts(@RequestParam String query, HttpServletRequest request) {
		try {
			Long userId = getUserIdFromToken(request);
			List<Contact> contacts = contactService.searchContacts(userId, query);
			return ResponseEntity.ok(contacts);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	
	  
	  // Get contact count
	  
	  @GetMapping("/count")
	 
	public ResponseEntity<?> getContactCount(HttpServletRequest request) {
		try {
			Long userId = getUserIdFromToken(request);
			long count = contactService.getContactCount(userId);

			Map<String, Long> response = new HashMap<>();
			response.put("count", count);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Helper method to extract user ID from JWT token
	private Long getUserIdFromToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("No valid token found");
		}

		String token = authHeader.substring(7);
		String username = jwtService.extractUsername(token);

		return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"))
				.getId();
	}
}