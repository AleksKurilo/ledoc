package dk.ledocsystem.ledoc.service;

import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

public interface EmailTemplateService {

    /**
     * Resolves {@link Template} and email subject by given key and locale stored in {@link LocaleContextHolder}.
     *
     * @param key Key to lookup template and subject
     */
    EmailTemplate getTemplateLocalized(String key);

    /**
     * Auxiliary class to encapsulate Freemarker's {@link Template email template} and subject.
     */
    @Getter
    @AllArgsConstructor
    abstract class EmailTemplate {
        Template template;
        String subject;

        public abstract String parseTemplate(Object modelObject);
    }
}
