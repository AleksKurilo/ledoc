package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.util.PeriodToHumanReadableConverter;
import dk.ledocsystem.service.api.dto.outbound.employee.EquipmentExportDTO;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import java.time.Period;

public class EquipmentToExportDtoMap extends PropertyMap<Equipment, EquipmentExportDTO> {

    @Override
    protected void configure() {

        map().setCategoryName(source.getCategory().getNameEn());
        map().setHomeLocation(source.getLocation().getName());

        Converter<Equipment, String> locationNameConverter = context -> context.getSource().getLoan() == null ? context.getSource().getLocation().getName() :
                context.getSource().getLoan().getLocation() == null ? context.getSource().getLocation().getName() : context.getSource().getLoan().getLocation().getName();

        using(locationNameConverter).map(source, destination.getCurrentLocation());
        map().setLoanStatus(source.getLoan() != null ? "Away" : "Home");
        map().setReviewResponsible("Coming soon");
        map().setSupplier(source.getSupplier().getName());

        Converter<ApprovalType, String> approvalTypeConverter = context -> context.getSource() == null ? "" : context.getSource().value();

        using(approvalTypeConverter).map(source.getApprovalType(), destination.getReviewStatus());

        PeriodToHumanReadableConverter converter = new PeriodToHumanReadableConverter();
        Converter<Period, String> reviewPeriodConverter = context -> context.getSource() == null ? "No" : "Yes, every " + converter.convert(context.getSource());
        using(reviewPeriodConverter).map(source.getApprovalRate(), destination.getMustBeReviewed());
        map().setAuthenticationType(source.getAuthenticationType().toString());
        map().setResponsible(source.getResponsible().getName());
    }
}
