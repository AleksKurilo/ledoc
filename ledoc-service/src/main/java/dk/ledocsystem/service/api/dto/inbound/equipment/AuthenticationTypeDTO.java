package dk.ledocsystem.service.api.dto.inbound.equipment;

import dk.ledocsystem.service.api.validation.equipment.UniqueAuthenticationTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AuthenticationTypeDTO {

    @NotNull
    @Size(max = 255)
    @UniqueAuthenticationTypeName
    private String nameEn;

    @Size(max = 255)
    private String nameDa;
}
