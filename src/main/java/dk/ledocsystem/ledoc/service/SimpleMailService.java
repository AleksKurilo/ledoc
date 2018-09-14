package dk.ledocsystem.ledoc.service;

import com.google.api.client.http.HttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

public interface SimpleMailService {

    ListenableFuture<HttpResponse> sendMimeMessage(String to, String subject, String body);
}
