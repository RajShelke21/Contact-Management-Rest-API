package com.contactapp.repository;

import com.contactapp.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserId(Long userId);
    Optional<Contact> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.phoneNo LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Contact> searchContacts(@Param("userId") Long userId, @Param("search") String search);
    
    long countByUserId(Long userId);
}