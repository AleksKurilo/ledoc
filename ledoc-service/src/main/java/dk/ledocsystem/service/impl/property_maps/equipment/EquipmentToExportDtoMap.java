package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentExportDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import dk.ledocsystem.service.impl.property_maps.converters.PeriodToHumanReadableConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

public class EquipmentToExportDtoMap extends PropertyMap<Equipment, EquipmentExportDTO> {

    @Override
    protected void configure() {

        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategoryName());
        map().setHomeLocation(source.getLocation().getName());

        Converter<Equipment, String> locationNameConverter = context -> context.getSource().getLoan() == null ? context.getSource().getLocation().getName() :
                context.getSource().getLoan().getLocation() == null ? context.getSource().getLocation().getName() : context.getSource().getLoan().getLocation().getName();

        using(locationNameConverter).map(source, destination.getCurrentLocation());
        map().setLoanStatus(source.getLoan() != null ? "Away" : "Home");
        map().setReviewResponsible("Coming soon");
        map().setSupplier(source.getSupplier().getName());

        Converter<ApprovalType, String> approvalTypeConverter = context -> context.getSource() == null ? "" : context.getSource().value();

        using(approvalTypeConverter).map(source.getApprovalType(), destination.getReviewStatus());

        using(PeriodToHumanReadableConverter.INSTANCE).map(source.getApprovalRate(), destination.getMustBeReviewed());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getAuthenticationType(), destination.getAuthenticationType());
        map().setResponsible(source.getResponsible().getName());
    }
}
