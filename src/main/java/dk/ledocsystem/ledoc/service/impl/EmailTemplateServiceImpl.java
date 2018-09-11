package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.service.EmailTemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final String SUBJECT_KEY_SUFFIX = ".email.subject";
    private static final String TEMPLATE_SUFFIX = ".ftl";
    private final Configuration freemarkerConfig;
    private final MessageSource messageSource;

    @Override
    public EmailTemplate getTemplateLocalized(String key) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        try {
            String subject = messageSource.getMessage(key + SUBJECT_KEY_SUFFIX, null, currentLocale);
            Template template = freemarkerConfig.getTemplate(key + TEMPLATE_SUFFIX, currentLocale);
            return new EmailTemplateImpl(template, subject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class EmailTemplateImpl extends EmailTemplate {

        EmailTemplateImpl(Template template, String subject) {
            super(template, subject);
        }

        @Override
        public String parseTemplate(Object modelObject) {
            try {
                return FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(), modelObject);
            } catch (IOException | TemplateException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
