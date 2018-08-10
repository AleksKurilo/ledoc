package dk.ledocsystem.ledoc.util;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.common.reflect.ClassPath;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.validation.Valid;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This utility checks all classes from "dto" package and confirms that all validation annotation
 * has messages defined in "messages.properties".
 */
public class ValidationMessageChecker {

    private static final String PACKAGE_NAME = "dk.ledocsystem.ledoc.dto";
    private static final String MESSAGE_RESOURCE_NAME = "messages.properties";
    private static final List<Class<? extends Annotation>> notValidatingAnnotations =
            Arrays.asList(Valid.class, JsonAlias.class);

    public static void main(String[] args) throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Properties messages = loadResources();

        ClassPath.from(loader)
                .getTopLevelClassesRecursive(PACKAGE_NAME)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .map(dtoClass -> checkClass(dtoClass, messages))
                .flatMap(List::stream)
                .forEach(System.out::println);
    }

    private static List<String> checkClass(Class<?> dtoClass, Properties properties) {
        List<String> missingKeys = new ArrayList<>();

        for (Field field : dtoClass.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (isValidatingAnnotation(annotation)) {
                    String messageKey = getAnnotationMessageKey(dtoClass, field, annotation);
                    if (!properties.containsKey(messageKey)) {
                        missingKeys.add(messageKey);
                    }
                }
            }
        }

        return missingKeys;
    }

    private static boolean isValidatingAnnotation(Annotation annotation) {
        return !notValidatingAnnotations.contains(annotation.annotationType());
    }

    private static String getAnnotationMessageKey(Class<?> dtoClass, Field field, Annotation annotation) {
        return annotation.annotationType().getSimpleName() + "." +
                Introspector.decapitalize(dtoClass.getSimpleName()) + "." +
                field.getName();
    }

    private static Properties loadResources() throws Exception {
        return PropertiesLoaderUtils.loadAllProperties(MESSAGE_RESOURCE_NAME);
    }
}
