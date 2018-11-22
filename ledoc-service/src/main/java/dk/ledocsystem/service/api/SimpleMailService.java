package dk.ledocsystem.service.api;

import com.google.api.client.http.HttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

public interface SimpleMailService {

    ListenableFuture<HttpResponse> sendMimeMessage(String to, String subject, String body);
}
