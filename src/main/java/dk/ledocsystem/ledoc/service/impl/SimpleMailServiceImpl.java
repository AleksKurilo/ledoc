package dk.ledocsystem.ledoc.service.impl;

import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
class SimpleMailServiceImpl implements SimpleMailService {
    private final JavaMailSender javaMailSender;
    private final Gmail gmail;

    @Async
    @Override
    public ListenableFuture<HttpResponse> sendMimeMessage(@NonNull String to, String subject, @NonNull String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body);
            Message gmailEmail = createGmailEmail(mimeMessage);
            Gmail.Users.Messages.Send request = gmail.users().messages().send("me", gmailEmail);
            HttpResponse response = request.buildHttpRequest()
                    .setUnsuccessfulResponseHandler(getHttpUnsuccessfulResponseHandler())
                    .execute();
            return AsyncResult.forValue(response);
        } catch (IOException | MessagingException e) {
            return AsyncResult.forExecutionException(e);
        }
    }

    private Message createGmailEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private MimeMessage createMimeMessage(String to, String subject, String body) throws MessagingException {
        MimeMessage email = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(email,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.toString());

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);
        mimeMessageHelper.setTo(to);

        return mimeMessageHelper.getMimeMessage();
    }

    private HttpUnsuccessfulResponseHandler getHttpUnsuccessfulResponseHandler() {
        ExponentialBackOff backoffPolicy = new ExponentialBackOff.Builder()
                .setMaxElapsedTimeMillis(300000)
                .build();
        return new HttpBackOffUnsuccessfulResponseHandler(backoffPolicy);
    }
}
