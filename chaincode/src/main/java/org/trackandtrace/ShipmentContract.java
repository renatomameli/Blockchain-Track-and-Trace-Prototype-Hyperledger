package org.trackandtrace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.trackandtrace.model.Shipment;
import org.trackandtrace.model.Status;

import java.io.UnsupportedEncodingException;

@Contract(name = "ShipmentContract")
@Default
public class ShipmentContract implements ContractInterface {

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createShipment(Context ctx, String shipmentId, Status status) {
        Shipment shipment = new Shipment();
        shipment.setShipmentId(shipmentId);
        shipment.setStatus(status);
        ctx.getStub().putStringState(shipmentId, shipment.toJSON());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void UpdateShipment(final Context ctx, String shipmentId, Status status) throws UnsupportedEncodingException, JsonProcessingException {
        ChaincodeStub stub = ctx.getStub();

        String shipmentState = stub.getStringState(shipmentId);

        if (shipmentState.isEmpty()) {
            String errorMessage = String.format("Shipment %s does not exist", shipmentId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Error");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Shipment shipment = objectMapper.readValue(shipmentState, Shipment.class);
        Shipment newShipment = new Shipment(shipment.getShipmentId(), status);

        String newShipmentState = objectMapper.writeValueAsString(newShipment);
        stub.putStringState(shipmentId, newShipmentState);
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readShipment(Context ctx, String shipmentId) {
        String shipmentJSON = ctx.getStub().getStringState(shipmentId);
        if (shipmentJSON == null || shipmentJSON.isEmpty()) {
            throw new ChaincodeException("Shipment not found");
        }
        return shipmentJSON;
    }
}
