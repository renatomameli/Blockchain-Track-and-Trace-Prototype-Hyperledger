package org.trackandtrace;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.trackandtrace.model.Organization;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ShipmentContractTest {

    @Mock
    private Context context;

    @Mock
    private ChaincodeStub stub;

    private ShipmentContract contract;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(context.getStub()).thenReturn(stub);

        contract = new ShipmentContract();
    }

    @Test
    void createShipment_success() {
        String object = "object";
        Date eta = new Date();
        Mockito.when(stub.getMspId()).thenReturn(Organization.CONSIGNOR.getDescription());

        assertDoesNotThrow(() -> contract.createShipment(context, object, eta));
    }

    @Test
    void createShipment_unauthorized() {
        String object = "object";
        Date eta = new Date();
        Mockito.when(stub.getMspId()).thenReturn(Organization.CONSIGNEE.getDescription());

        assertThrows(ChaincodeException.class, () -> contract.createShipment(context, object, eta));
    }

}
