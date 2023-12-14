package com.example.Backend.service;

import com.example.Backend.entity.Verification;
import com.example.Backend.repository.VerificationRepository;
import jakarta.mail.internet.InternetAddress;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final VerificationRepository verificationRepository;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(text);
            InternetAddress from = new InternetAddress("malacprodavac@gmail.com", "Malac Prodavac");
            simpleMailMessage.setFrom(from.toString());
            javaMailSender.send(simpleMailMessage);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getVerification(String email, String code) {
        Optional<Verification> verification =  verificationRepository.findVerificationByEmailAndCode(email, code);
        if (verification.isEmpty())
            return false;
        return true;
    }

    public void saveVerification(String email, String code) {
        Optional<Verification> existingVerification = verificationRepository.findByEmail(email);

        if (existingVerification.isPresent()) {
            Verification verification = existingVerification.get();
            verification.setCode(code);
            verificationRepository.save(verification); // Azuriranje koda
        }
        else {
            Verification verification = new Verification();
            verification.setEmail(email);
            verification.setCode(code);
            verificationRepository.save(verification); // Dodavanje novog emaila i koda
        }
    }
}
