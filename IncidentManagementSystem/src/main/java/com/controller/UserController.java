package com.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dto.admin.CreateUserRequest;
import com.dto.user.LoginRequest;
import com.dto.user.LoginResponse;
import com.entity.User;
import com.service.admin.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@RestController
@CrossOrigin(origins = { "https://incident-management-frontend.vercel.app" }, allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User savedUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
	
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response, HttpSession session) {
    try {
        LoginResponse loginResponse = userService.loginUser(request.getUsername(), request.getPassword());

        // store user in session
        session.setAttribute("user", loginResponse);

        // session id
        String sessionId = session.getId();

        // Send JSESSIONID cookie manually (for cross-site)
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", sessionId)
                .httpOnly(true)
                .secure(true)   // true ONLY for https
                .path("/")
                .sameSite("None")   // REQUIRED for cross-site cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);

    } catch (RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }
	
    @PutMapping("/assign-role")
    public ResponseEntity<?> assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            userService.assignRole(userId, roleId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Role assigned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<User>> getUsersForDropdown() {
        List<User> users = userService.findByRoleName("ANALYST"); 
        return ResponseEntity.ok(users != null ? users : Collections.emptyList());
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        User user = userService.updateUser(updatedUser);
        return ResponseEntity.ok(user);
    }
	
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
	
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.sendPasswordResetOTP(email);
        return ResponseEntity.ok("OTP sent to your email.");
    }
	
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset.");
    }
	
    @PostMapping("/forgot-username")
    public ResponseEntity<String> forgotUsername(@RequestParam String email) {
        try {
            userService.sendUsernameToEmail(email);
            return ResponseEntity.ok("Your username has been sent to your registered email.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
	
    @GetMapping("/findByName")
    public ResponseEntity<User> findByName(@RequestParam String name) {
        try {
            User user = userService.findByName(name);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
	
    @GetMapping("/findAllByName")
    public ResponseEntity<List<User>> findAllByName(@RequestParam String name) {
        List<User> users = userService.findAllByName(name);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(users);
    }
	
    @GetMapping("/getAnalysts")
    public ResponseEntity<List<User>> getAnalysts() {
        List<User> analysts = userService.findByRoleName("ANALYST");
        return ResponseEntity.ok(analysts != null ? analysts : Collections.emptyList());
    }
    	
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        LoginResponse user = (LoginResponse) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/analysts")
    public ResponseEntity<List<User>> getAllAnalysts() {
        List<User> analysts = userService.getAllAnalystUsers();
        return ResponseEntity.ok(analysts);
    }
}
