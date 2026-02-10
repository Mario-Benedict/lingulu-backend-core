package com.lingulu.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification OTP");
        message.setText(
            "Kode OTP Anda: " + otp + "\n" +
            "Berlaku selama 10 menit."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Password - Lingulu");
        message.setText(
            "Halo,\n\n" +
            "Anda telah meminta untuk mereset password akun Anda.\n\n" +
            "Klik link berikut untuk mereset password Anda:\n" +
            resetLink + "\n\n" +
            "Link ini berlaku selama 1 jam.\n\n" +
            "Jika Anda tidak meminta reset password, abaikan email ini.\n\n" +
            "Salam,\n" +
            "Tim Lingulu"
        );

        mailSender.send(message);
    }
}

