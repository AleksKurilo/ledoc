package dk.ledocsystem.service.api;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;

public interface EmailTemplateService {

    /**
     * Resolves {@link Template} and email subject by given key and locale stored in {@link LocaleContextHolder}.
     *
     * @param key Key to resolve template and subject
     * @throws IOException            if template with the given name was not found or any reading error occurred
     * @throws NoSuchMessageException if the email subject with specified key was not found
     */
    EmailTemplate getTemplateLocalized(String key) throws IOException, NoSuchMessageException;

    /**
     * Auxiliary class to encapsulate Freemarker's {@link Template email template} and subject.
     */
    @Getter
    @AllArgsConstructor
    abstract class EmailTemplate {
        Template template;
        String subject;

        public abstract String parseTemplate(Object modelObject) throws IOException, TemplateException;
    }
}
