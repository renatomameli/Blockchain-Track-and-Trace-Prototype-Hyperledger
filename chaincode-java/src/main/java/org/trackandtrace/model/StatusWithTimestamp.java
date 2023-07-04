package org.trackandtrace.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusWithTimestamp {

    private Status status;
    private LocalDateTime localDateTime;

}
