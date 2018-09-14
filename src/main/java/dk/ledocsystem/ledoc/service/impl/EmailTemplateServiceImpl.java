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

    private static final String TEMPLATE_SUFFIX = ".ftl";
    private final Configuration freemarkerConfig;
    private final MessageSource messageSource;

    @Override
    public EmailTemplate getTemplateLocalized(String key) throws IOException {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage(key, null, currentLocale);
        Template template = freemarkerConfig.getTemplate(key + TEMPLATE_SUFFIX, currentLocale);
        return new EmailTemplateImpl(template, subject);
    }

    private static class EmailTemplateImpl extends EmailTemplate {

        EmailTemplateImpl(Template template, String subject) {
            super(template, subject);
        }

        @Override
        public String parseTemplate(Object modelObject) throws IOException, TemplateException {
            return FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate(), modelObject);
        }
    }
}
