package org.trackandtrace.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StatusWithTimestamp {

    private Status status;
    private LocalDateTime localDateTime;

}
