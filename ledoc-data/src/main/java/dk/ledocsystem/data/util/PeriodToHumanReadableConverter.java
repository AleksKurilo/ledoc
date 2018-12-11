package dk.ledocsystem.data.util;

import lombok.NoArgsConstructor;

import java.time.Period;

@NoArgsConstructor
public class PeriodToHumanReadableConverter {
    public String convert(Period period) {
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
