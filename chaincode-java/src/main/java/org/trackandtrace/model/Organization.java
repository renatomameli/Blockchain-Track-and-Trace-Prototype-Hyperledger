package org.trackandtrace.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Organization {
    CONSIGNOR (0,"ConsignorMSP"),
    CUSTOMS(1,"CustomsMSP"),
    PRE_CARRIAGE_SHIPPER(2,"PreCarriageShipperMSP"),
    MAIN_CARRIER(3,"MainCarrierMSP"),
    ON_CARRIAGE_SHIPPER(4,"OnCarriageShipperMSP"),
    CONSIGNEE(5,"ConsigneeMSP");

    private final int order;
    private final String description;

    public static Organization fromDescription(String description) {
        for (Organization org : Organization.values()) {
            if (org.getDescription().equals(description)) {
                return org;
            }
        }
        throw new IllegalArgumentException("No organization with description " + description + " found");
    }
}
