package dk.ledocsystem.ledoc.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.temporal.Temporal;

public final class BeanCopyUtils {

    private static final BeanUtilsBean standardBeanUtils = new ExtendedBeanUtils();
    private static final BeanUtilsBean nullAwareBeanUtils = new NullAwareBeanUtils();

    /**
     * Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.
     *
     * @param source      Origin bean whose properties are retrieved
     * @param destination Destination bean whose properties are modified
     */
    public static void copyProperties(Object source, Object destination) {
        copyProperties(source, destination, true);
    }

    /**
     * Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.
     *
     * @param source      Origin bean whose properties are retrieved
     * @param destination Destination bean whose properties are modified
     * @param copyNulls   if {@code false}, properties of {@code source} with value equal to {@literal null}
     *                    will not be copied to {@code destination}
     */
    public static void copyProperties(Object source, Object destination, boolean copyNulls) {
        try {
            if (!copyNulls) {
                nullAwareBeanUtils.copyProperties(destination, source);
            } else {
                standardBeanUtils.copyProperties(destination, source);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ExtendedBeanUtils extends BeanUtilsBean {

        /**
         * Uses deep copying of nested properties instead of shallow copying of base class.
         */
        @Override
        public void copyProperty(Object bean, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (value == null || isSimpleProperty(value.getClass())) {
                super.copyProperty(bean, name, value);
            } else {
                try {
                    Object property = PropertyUtils.getProperty(bean, name);
                    if (property != null) {
                        copyProperties(property, value);
                    }
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private static boolean isSimpleProperty(Class<?> clazz) {
            return BeanUtils.isSimpleProperty(clazz) || Temporal.class.isAssignableFrom(clazz);
        }
    }

    private static class NullAwareBeanUtils extends ExtendedBeanUtils {

        @Override
        public void copyProperty(Object bean, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                super.copyProperty(bean, name, value);
            }
        }
    }
}
