package org.trackandtrace.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String shipmentId;
    private Status status;

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
