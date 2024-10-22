package com.project.demo.rest.user;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> usersPage = userRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(usersPage.getTotalPages());
        meta.setTotalElements(usersPage.getTotalElements());
        meta.setPageNumber(usersPage.getNumber() + 1);
        meta.setPageSize(usersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Users retrieved successfully",
                usersPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new GlobalResponseHandler().handleResponse("User created successfully",
                user, HttpStatus.CREATED, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return new GlobalResponseHandler().handleResponse("User retrieved successfully",
                    user.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User with id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            User existingUser = foundUser.get();
            existingUser.setName(user.getName());
            existingUser.setLastname(user.getLastname());
            existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(existingUser);
            return new GlobalResponseHandler().handleResponse("User updated successfully",
                    existingUser, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User with id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            userRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("User deleted successfully",
                    foundUser.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User with id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> authenticatedUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return new GlobalResponseHandler().handleResponse("Authenticated user retrieved successfully",
                user, HttpStatus.OK, request);
    }
}
