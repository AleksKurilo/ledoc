package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.equipment.EquipmentLoan;
import dk.ledocsystem.service.api.dto.outbound.employee.EquipmentExportDTO;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

public class EquipmentToExportDtoMap extends PropertyMap<Equipment, EquipmentExportDTO> {

    @Override
    protected void configure() {

        map().setCategoryName(source.getCategory().getId().toString());
        map().setHomeLocation(source.getLocation().getName());

//        Converter<EquipmentLoan, String> locationNameConverter = context -> context.getSource() == null ? source.getLocation().getName() :
//                context.getSource().getLocation() == null ? source.getLocation().getName() : context.getSource().getLocation().getName();

        Converter<EquipmentLoan, String> locationNameConverter = context -> {
            if (context.getSource() == null) {
                return source.getLocation().getName();
            } else {
                if (context.getSource().getLocation() == null) {
                    return source.getLocation().getName();
                } else {
                    return context.getSource().getLocation().getName();
                }
            }
        };

        using(locationNameConverter).map(source.getLoan(), destination.getCurrentLocation());
        map().setLoanStatus(source.getLoan() != null ? "Away" : "Home");
        map().setReviewResponsible("Coming soon");
        map().setSupplier(source.getSupplier().getName());

        Converter<ApprovalType, String> approvalTypeConverter = context -> context.getSource() == null ? "" : context.getSource().value();

        using(approvalTypeConverter).map(source.getApprovalType(), destination.getReviewStatus());
        map().setMustBeReviewed("Every month");
        map().setAuthenticationType(source.getAuthenticationType().toString());
        map().setResponsible(source.getResponsible().getName());
    }
}
