package com.contactapp.service;

import com.contactapp.dto.ContactDto;
import com.contactapp.entity.Contact;
import com.contactapp.entity.User;
import com.contactapp.repository.ContactRepository;
import com.contactapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    
    public List<Contact> getAllContacts(Long userId) {
        return contactRepository.findByUserId(userId);
    }
    
    public Contact getContactById(Long id, Long userId) {
        return contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
    }
    
    public Contact createContact(ContactDto contactDto, Long userId, MultipartFile photo) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Contact contact = new Contact();
        contact.setName(contactDto.getName());
        contact.setPhoneNo(contactDto.getPhoneNo());
        contact.setEmail(contactDto.getEmail());
        contact.setGender(contactDto.getGender());
        contact.setUser(user);
        
        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoPath = savePhoto(photo);
            contact.setPhoto(photoPath);
        }
        
        return contactRepository.save(contact);
    }
    
    public Contact updateContact(Long id, ContactDto contactDto, Long userId, MultipartFile photo) {
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        contact.setName(contactDto.getName());
        contact.setPhoneNo(contactDto.getPhoneNo());
        contact.setEmail(contactDto.getEmail());
        contact.setGender(contactDto.getGender());
        
        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoPath = savePhoto(photo);
            contact.setPhoto(photoPath);
        }
        
        return contactRepository.save(contact);
    }
    
    public void deleteContact(Long id, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        // Delete photo file if exists
        if (contact.getPhoto() != null) {
            deletePhoto(contact.getPhoto());
        }
        
        contactRepository.delete(contact);
    }
    
    public List<Contact> searchContacts(Long userId, String search) {
        return contactRepository.searchContacts(userId, search);
    }
    
    public long getContactCount(Long userId) {
        return contactRepository.countByUserId(userId);
    }
    
    private String savePhoto(MultipartFile photo) {
        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(photo.getInputStream(), filePath);
            
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo: " + e.getMessage());
        }
    }
    
    private void deletePhoto(String photoPath) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + photoPath.replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete photo: " + e.getMessage());
        }
    }
}