package com.chillies.hearttohome.controllers;


import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.DTO.UserDTO;
import com.chillies.hearttohome.models.Role;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.models.User;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.services.Services;
import com.chillies.hearttohome.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final Services services;

    @Autowired
    UserService userService;

    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(),
                HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId,
                                                 @RequestParam String roleName) {
        userService.updateUserRole(userId, roleName);
        return ResponseEntity.ok("User role updated");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id),
                HttpStatus.OK);
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return userService.getAllRoles();
    }
    @PutMapping("/update-expiry-status")
    public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId,
                                                            @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(userId, expire);
        return ResponseEntity.ok("Account expiry status updated");
    }

    @PutMapping("/update-enabled-status")
    public ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId,
                                                             @RequestParam boolean enabled) {
        userService.updateAccountEnabledStatus(userId, enabled);
        return ResponseEntity.ok("Account enabled status updated");
    }
    @PutMapping("/update-lock-status")
    public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId,
                                                          @RequestParam boolean lock) {
        userService.updateAccountLockStatus(userId, lock);
        return ResponseEntity.ok("Account lock status updated");
    }

    @PutMapping("/update-credentials-expiry-status")
    public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(userId, expire);
        return ResponseEntity.ok("Credentials expiry status updated");
    }


    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam Long userId,
                                                 @RequestParam String password) {
        try {
            userService.updatePassword(userId, password);
            return ResponseEntity.ok("Password updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/add-service")
    public ResponseEntity<ServiceEntity> addService(@RequestBody ServiceDTO serviceDTO) {
        return services.addService(serviceDTO);
    }

    @GetMapping("/title-exists")
    public boolean titleExists(@RequestParam String title) {
        return services.titleExists(title);
    }

    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {

        services.deleteService(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disabled-services")
    public List<ServiceEntity> getDisabledServices() {
        return services.getDisabledServices();
    }
    @PutMapping("/enable-service/{id}")
    public ResponseEntity<Void> enableService(@PathVariable Long id) {

        services.enableService(id);

        return ResponseEntity.ok().build();
    }

}
