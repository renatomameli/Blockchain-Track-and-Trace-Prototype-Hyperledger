package org.trackandtrace.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    CREATED ("Created"),
    CUSTOMS_APPROVED("Approved"),
    PRE_CARRIAGE_START("Pre Carriage Start"),
    MAIN_CARRIAGE_START("Main Carriage Start"),
    ON_CARRIAGE_START("On Carriage Start"),
    DELIVERED("Delivered");

    private final String description;
}
