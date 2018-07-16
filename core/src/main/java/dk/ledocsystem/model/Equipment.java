package dk.ledocsystem.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Setter
@Getter
public class Equipment {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;

    private String idNumber;

    private String serialNumber;

    private String localId;

    private Long categoryId;

    private Long creatorId;

    private String manufacturer;

    private BigDecimal price;

    private BigInteger supplierId;

    private Status status;

    private Date purchaseDate;

    private Boolean archived;

    private String archiveReason;
}
