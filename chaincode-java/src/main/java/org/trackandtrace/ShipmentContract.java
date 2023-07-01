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
import org.trackandtrace.model.Organization;
import org.trackandtrace.model.Shipment;
import org.trackandtrace.model.Status;
import org.trackandtrace.model.StatusWithTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Contract(name = "ShipmentContract")
@Default
public class ShipmentContract implements ContractInterface {

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createShipment(Context ctx, String object, Date eta) {
        if (this.getOrganization(ctx.getStub()) != Organization.CONSIGNOR) {
            throw new ChaincodeException("Only the Consignor is authorized to create a shipment");
        }

        String generatedId = UUID.randomUUID().toString();
        Shipment shipment = new Shipment();
        shipment.setObject(object);
        shipment.setEta(eta);
        shipment.setShipmentId(generatedId);
        shipment.setStatus(new StatusWithTimestamp(Status.CREATED, LocalDateTime.now()));
        ctx.getStub().putStringState(generatedId, shipment.toJSON());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateShipment(final Context ctx, String shipmentId, Status status, String coordinates) throws JsonProcessingException {
        ChaincodeStub stub = ctx.getStub();

        String shipmentState = stub.getStringState(shipmentId);

        if (shipmentState.isEmpty()) {
            String errorMessage = String.format("Shipment %s does not exist", shipmentId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Error");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Shipment shipment = objectMapper.readValue(shipmentState, Shipment.class);

        if (!this.isSubmitterAuthorizedAndStatusValid(shipment.getStatus().getStatus(), status, this.getOrganization(stub))) {
            throw new ChaincodeException("You are not authorized to update the status of the shipment");
        }

        Shipment newShipment = new Shipment();
        newShipment.setShipmentId(shipment.getShipmentId());
        newShipment.setStatus(new StatusWithTimestamp(status, LocalDateTime.now()));
        newShipment.setCoordinates(coordinates);

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

    private Organization getOrganization(ChaincodeStub stub) {
        String submitterMspId = stub.getMspId();
        return Organization.fromDescription(submitterMspId);
    }

    private boolean isSubmitterAuthorizedAndStatusValid(Status currentShipmentStatus, Status newStatus, Organization submitterOrg) {
        if (currentShipmentStatus.getOrder() == submitterOrg.getOrder()) {
            return currentShipmentStatus == newStatus;
        }

        if (currentShipmentStatus.getOrder() + 1 == submitterOrg.getOrder()) {
            return currentShipmentStatus.getOrder() + 1 == newStatus.getOrder();
        }

        return false;
    }
}
