package com.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dto.admin.CreateUserRequest;
import com.dto.user.LoginResponse;
import com.entity.Role;
import com.entity.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import com.service.shared.EmailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User createUser(CreateUserRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        Role roleToAssign = null;
        if (request.getRoleId() != null) {
            roleToAssign = roleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        } else {
            roleToAssign = roleRepo.findAll().stream()
                    .filter(r -> "USER".equalsIgnoreCase(r.getName()) || "END_USER".equalsIgnoreCase(r.getName()))
                    .findFirst()
                    .orElse(null);
        }

        if (roleToAssign != null) user.setRole(roleToAssign);

        User saved = userRepo.save(user);
        return saved;
    }

    public LoginResponse loginUser(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account inactive");
        }

        if (user.getRole() == null || !user.getRole().isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no active role assigned");
        }

        LoginResponse response = new LoginResponse();
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());
        return response;
    }

    @Transactional
    public void assignRole(Long userId, Long roleId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        user.setRole(role);
        userRepo.save(user);

        emailService.sendRoleAssignmentEmail(user.getEmail(), role.getName());
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void sendPasswordResetOTP(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        user.setResetToken(token);
        user.setTokenExpiry(expiry);
        userRepo.save(user);

        emailService.sendEmail(user.getEmail(), "Password Reset OTP", "Your token is: " + token);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepo.save(user);
    }

    public User updateUser(User updatedUser) {
        User user = userRepo.findById(updatedUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setName(updatedUser.getName());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepo.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepo.deleteById(userId);
    }

    public void sendUsernameToEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with that email"));

        String subject = "Your Username - Incident Management System";
        String body = "Hi,\n\nYour username is: " + user.getUsername() + "\n\nRegards,\nIncident Management Team";
        emailService.sendEmail(email, subject, body);
    }

    public User findByName(String name) {
        return userRepo.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with name: " + name));
    }

    public List<User> findAllByName(String name) {
        return userRepo.findAllByName(name);
    }

    public List<User> findByRoleName(String roleName) {
        return userRepo.findByRole_Name(roleName);
    }

    public Optional<User> findByUsername(String userName) {
        return userRepo.findByUsername(userName);
    }
    
    /**
     * Retrieves all active users who have the role "ANALYST".
     * * @return A list of User entities with the role name "ANALYST".
     */
    public List<User> getAllAnalystUsers() {
        // Assuming the role name is exactly "ANALYST". 
        // Case sensitivity depends on your database and JPA setup.
        return userRepo.findByRole_Name("ANALYST"); 
    }
    
    
}
