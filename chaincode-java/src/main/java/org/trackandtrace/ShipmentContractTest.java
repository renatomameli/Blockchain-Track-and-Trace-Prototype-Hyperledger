package org.trackandtrace;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.trackandtrace.model.Organization;
import org.trackandtrace.model.Shipment;
import org.trackandtrace.model.Status;
import org.trackandtrace.model.StatusWithTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ShipmentContractTest {

    @Mock
    private Context context;

    @Mock
    private ChaincodeStub stub;

    private ShipmentContract contract;

    private String shipmentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getStub()).thenReturn(stub);
        contract = new ShipmentContract();
        shipmentId = UUID.randomUUID().toString();
    }

    @Test
    void createShipment_success() {
        String object = "object";
        Date eta = new Date();
        when(stub.getMspId()).thenReturn(Organization.CONSIGNOR.getDescription());

        assertDoesNotThrow(() -> contract.createShipment(context, object, eta));
    }

    @Test
    void createShipment_unauthorized() {
        String object = "object";
        Date eta = new Date();
        when(stub.getMspId()).thenReturn(Organization.CONSIGNEE.getDescription());

        assertThrows(ChaincodeException.class, () -> contract.createShipment(context, object, eta));
    }

    @Test
    void updateShipmentTest() {
        Shipment shipment = new Shipment();
        shipment.setStatus(new StatusWithTimestamp(Status.CREATED, LocalDateTime.now()));
        shipment.setShipmentId(shipmentId);
        when(stub.getStringState(shipmentId)).thenReturn(shipment.toJSON());

        when(stub.getMspId()).thenReturn(Organization.CUSTOMS.getDescription());

        assertDoesNotThrow(() -> contract.updateShipment(context, shipmentId, Status.CUSTOMS_APPROVED, "coordinates"));

        verify(stub).putStringState(eq(shipmentId), any(String.class));
    }

    @Test
    void readShipmentTest() {
        Shipment shipment = new Shipment();
        shipment.setStatus(new StatusWithTimestamp(Status.CREATED, LocalDateTime.now()));
        shipment.setShipmentId(shipmentId);
        when(stub.getStringState(shipmentId)).thenReturn(shipment.toJSON());

        assertDoesNotThrow(() -> contract.readShipment(context, shipmentId));
    }

    @Test
    void readShipmentNotFoundTest() {
        when(stub.getStringState(shipmentId)).thenReturn(null);

        assertThrows(ChaincodeException.class, () -> contract.readShipment(context, shipmentId));
    }
}
