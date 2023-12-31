package org.trackandtrace.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String shipmentId;
    private StatusWithTimestamp status;
    private String coordinates;
    private String object;
    private Date eta;

    public String toJSON() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Shipment fromJSON(String json) {
        try {
            return objectMapper.readValue(json, Shipment.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
