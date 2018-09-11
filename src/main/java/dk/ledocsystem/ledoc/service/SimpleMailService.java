package dk.ledocsystem.ledoc.service;

public interface SimpleMailService {

    void sendMimeMessage(String to, String subject, String body);
}
