package com.contactapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ContactDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNo;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;
    
    // Constructors
    public ContactDto() {}
    
    public ContactDto(String name, String phoneNo, String email, String gender) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.gender = gender;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}