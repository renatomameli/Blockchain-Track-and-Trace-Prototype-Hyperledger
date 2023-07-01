package org.trackandtrace.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    // Status are ordered as in the freight process
    CREATED (0,"Created"),
    CUSTOMS_APPROVED(1,"Approved"),
    PRE_CARRIAGE_START(2,"Pre Carriage Start"),
    MAIN_CARRIAGE_START(3,"Main Carriage Start"),
    ON_CARRIAGE_START(4,"On Carriage Start"),
    DELIVERED(5,"Delivered");

    private final int order;
    private final String description;
}
