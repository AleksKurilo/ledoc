package dk.ledocsystem.service.impl.property_maps.converters;

import org.modelmapper.AbstractConverter;

import java.time.Period;

public class PeriodToHumanReadableConverter extends AbstractConverter<Period, String> {

    public static final PeriodToHumanReadableConverter INSTANCE = new PeriodToHumanReadableConverter();

    @Override
    public String convert(Period source) {
        return (source == null) ? "No" : "Yes, every " + convertInternal(source);
    }

    private String convertInternal(Period period) {
        StringBuilder sb = new StringBuilder();
        if (period.getYears() > 0) {
            sb.append(period.getYears() + (period.getYears() == 1 ? " year " : " years "));
        }
        if (period.getMonths() > 0) {
            sb.append(period.getMonths() + (period.getMonths() == 1 ? " month " : " months "));
        }
        if (period.getDays() > 0) {
            sb.append(period.getDays() + (period.getDays() == 1 ? " day" : " days"));
        }
        return sb.toString();
    }
}
