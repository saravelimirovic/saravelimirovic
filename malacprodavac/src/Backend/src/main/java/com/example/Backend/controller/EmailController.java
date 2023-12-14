package com.example.Backend.controller;

import com.example.Backend.dto.EmailDTO;
import com.example.Backend.dto.LoginDTO;
import com.example.Backend.dto.VerificationDTO;
import com.example.Backend.entity.User;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.service.EmailService;
import com.example.Backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/email")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmailController {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //    @PreAuthorize("hasRole(T(com.example.Backend.entity.UserRoles).ADMIN)")
    @PostMapping("/send_code")
    public ResponseEntity<Object> sendCode(@RequestBody EmailDTO email) {
        Random random = new Random();
        Integer randomNumber = 1000 + random.nextInt(9000);
        String randomString = randomNumber.toString();
        emailService.sendEmail(email.getEmail(), "Verifikacija email adrese", "Vas verifikacioni kod je: " + randomNumber + ". Molimo unesite kod na odgovarajuće mesto u aplikaciji. Hvala!");
        emailService.saveVerification(email.getEmail(), randomString);
        return ResponseEntity.ok(Map.of("Code", randomString));
    }

    @PostMapping("/check_code")
    public ResponseEntity<Object> checkCode(@RequestBody VerificationDTO dto) {
        boolean is = emailService.getVerification(dto.getEmail(), dto.getCode());
        if(is) return ResponseEntity.ok(Map.of("IsValid", is));
        else return ResponseEntity.badRequest().body("No params provided");
    }

    @GetMapping("/check_user")
    public ResponseEntity<Object> checkUserEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }
        return ResponseEntity.ok(Map.of("Message", 1));
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Object> changePassword(@RequestBody LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        if(user != null) {
            user.setPassword(passwordEncoder.encode(dto.getLozinka()));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("Message", 1));
        }
        throw new IllegalArgumentException("This user does not exist.");
    }
}