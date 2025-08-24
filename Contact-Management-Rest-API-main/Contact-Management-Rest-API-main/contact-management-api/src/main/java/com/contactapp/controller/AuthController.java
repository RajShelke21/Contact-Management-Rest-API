
package com.contactapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contactapp.dto.AuthRequest;
import com.contactapp.dto.ForgotPasswordRequest;
import com.contactapp.dto.ResetPasswordRequest;
import com.contactapp.dto.UserDto;
import com.contactapp.entity.User;
import com.contactapp.service.AuthService;
import com.contactapp.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthRequest request) {
        try {
            UserDto user = authService.register(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody AuthRequest request) {
        try {
            User user = authService.authenticate(request);
            String token = jwtService.generateToken(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            // FIXED: Call generateResetToken instead of forgetPassword
            String resetToken = authService.generateResetToken(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reset password link has been sent to your email");
            response.put("resetToken", resetToken); // For testing only - remove in production
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            System.out.println("🔥🔥🔥 CONTROLLER: Reset password request received");
            System.out.println("🔥🔥🔥 CONTROLLER: Token: " + request.getToken());
            System.out.println("🔥🔥🔥 CONTROLLER: New Password: " + request.getNewPassword());
            
            // Call service method to reset password
            authService.resetPassword(request.getToken(), request.getNewPassword());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("🔥🔥🔥 CONTROLLER ERROR: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}