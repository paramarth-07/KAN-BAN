package com.example.kanban.service;

import com.example.kanban.dto.UserRegistrationDto;
import com.example.kanban.exception.ResourceNotFoundException;
import com.example.kanban.model.Role;
import com.example.kanban.model.User;
import com.example.kanban.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username '" + registrationDto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email '" + registrationDto.getEmail() + "' is already registered");
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername().trim());
        user.setEmail(registrationDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void toggleUserRole(Long id, String currentAdminUsername) {
        User user = findById(id);
        if (user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("You cannot modify your own administrative role");
        }

        if (user.getRole() == Role.ROLE_ADMIN) {
            user.setRole(Role.ROLE_USER);
        } else {
            user.setRole(Role.ROLE_ADMIN);
        }
        userRepository.save(user);
    }

    public void deleteUser(Long id, String currentAdminUsername) {
        User user = findById(id);
        if (user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }
        if (user.getBoards() != null) {
            for (com.example.kanban.model.Board board : user.getBoards()) {
                if (board.getNotes() != null) {
                    for (com.example.kanban.model.Note note : board.getNotes()) {
                        note.setBoard(null);
                    }
                }
            }
        }
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
