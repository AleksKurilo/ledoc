package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.equipment.UniqueAuthenticationTypeName;
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
