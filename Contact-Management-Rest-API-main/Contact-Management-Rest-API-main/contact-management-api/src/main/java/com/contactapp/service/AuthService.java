package com.contactapp.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.contactapp.dto.AuthRequest;
import com.contactapp.dto.UserDto;
import com.contactapp.entity.User;
import com.contactapp.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    public AuthService() {
        System.out.println("AuthService instance created: " + this.hashCode());
    }
    
    
    
    
    
 // Replace your existing register method with this:
    public UserDto register(AuthRequest request) {
        System.out.println("🔥🔥🔥 REGISTER START");
        System.out.println("🔥🔥🔥 Username: " + request.getUsername());
        System.out.println("🔥🔥🔥 Raw Password: " + request.getPassword());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        System.out.println("🔥🔥🔥 Encoded Password: " + encodedPassword);
        System.out.println("🔥🔥🔥 Encoded Length: " + encodedPassword.length());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(request.getEmail());
        
        User savedUser = userRepository.save(user);
        System.out.println("🔥🔥🔥 REGISTER END - Saved password: " + savedUser.getPassword());
        
        return new UserDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
    
    

    
 // Replace your existing authenticate method with this:
    public User authenticate(AuthRequest request) {
        System.out.println("🔥🔥🔥 LOGIN START");
        System.out.println("🔥🔥🔥 Login Username: " + request.getUsername());
        System.out.println("🔥🔥🔥 Login Password: " + request.getPassword());
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("🔥🔥🔥 Stored Hash: " + user.getPassword());
        System.out.println("🔥🔥🔥 Hash Length: " + user.getPassword().length());
        
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("🔥🔥🔥 Password Matches: " + matches);
        
        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }
        
        System.out.println("🔥🔥🔥 LOGIN SUCCESS");
        return user;
    }

    
    
   
    
    public String forgetPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with given email does not exist");
        }

        User user = optionalUser.get();

        // Generate a reset token (for simplicity, use UUID)
        String token = UUID.randomUUID().toString();

        // Store the token in DB (optional: create resetToken field or map manually)
        user.setResetToken(token);
        userRepository.save(user);

        // Compose the reset link
        String resetLink = "http://localhost:8443/api/auth/reset?token=" + token;

        // Simulate sending email (print or log it)
        System.out.println("Reset link (send to email): " + resetLink);

        return "Reset password link has been sent to your email (check logs)";
    }  
    
    
    
    @PostConstruct
    public void testPasswordEncoder() {
        System.out.println("🔍 PasswordEncoder Test:");
        System.out.println("PasswordEncoder class: " + passwordEncoder.getClass().getName());
        String test = passwordEncoder.encode("test123");
        System.out.println("Test encoding result: " + test);
        System.out.println("Test encoding length: " + test.length());
        boolean matches = passwordEncoder.matches("test123", test);
        System.out.println("Test matches: " + matches);
    }
    
    

    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Debug logging
        System.out.println("Starting password reset...");
        System.out.println("Token: " + token);
        System.out.println("New password: " + newPassword);
        System.out.println("PasswordEncoder: " + passwordEncoder);
        
        // Find user by reset token
        User user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));
        
        System.out.println("Found user: " + user.getUsername());
        System.out.println("Current password: " + user.getPassword());
        
        // Check token 
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        // Encode new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("Is properly encoded: " + encodedPassword.startsWith("$2a$"));
        
        // Update user
        user.setPassword(encodedPassword);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        // Save and verify
        User savedUser = userRepository.save(user);
        System.out.println("Saved password: " + savedUser.getPassword());
        
        // Verify the save worked
        User verifyUser = userRepository.findById(user.getId()).orElse(null);
        if (verifyUser != null) {
            System.out.println("Verification - DB password: " + verifyUser.getPassword());
        }
        
        System.out.println("Password reset completed successfully!");
    }
    
    
    
    
    
    
    
 // Replace your existing generateResetToken method with this:
    public String generateResetToken(String email) {
        System.out.println("🔥🔥🔥 FORGOT PASSWORD START");
        System.out.println("🔥🔥🔥 Email: " + email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        
        userRepository.save(user);
        System.out.println("🔥🔥🔥 FORGOT PASSWORD END - Token: " + resetToken);
        
        return resetToken;
    }
    
    
    
    
}
