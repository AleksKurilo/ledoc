package dk.ledocsystem.ledoc.service;

import org.springframework.mail.SimpleMailMessage;

public interface SimpleMailService {

    void sendEmail(SimpleMailMessage email);

    void sendEmail(String from, String to, String subject, String body);
}
