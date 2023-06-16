# Environment variables
export FABRIC_CFG_PATH=/Users/renato.mameli/Hyperledger/trackandtrace
export PATH=~/Hyperledger/fabric-samples/bin:$PATH

docker compose down
rm -r channel-artifacts
rm -r crypto-config
rm -r system-genesis-block


# Step 1
cryptogen generate --config=./crypto-config.yaml

# Step 2
configtxgen -profile SampleMultiNodeEtcdRaft -channelID system-channel -outputBlock ./system-genesis-block/genesis.block

# Step 3
configtxgen -profile Frachtverfolgung -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID trackandtracechannel

# Step 4
docker-compose up -d

sleep 5

# Step 5
docker exec -it peer0.consignor.trackandtrace.com peer channel create -o orderer.trackandtrace.com:7050 -c trackandtracechannel -f /etc/hyperledger/fabric/channel-artifacts/channel.tx --tls true --cafile /etc/hyperledger/fabric/orderer/tlsca.trackandtrace.com-cert.pem

# Step 6
docker exec -it peer0.consignor.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Step 7
docker cp peer0.consignor.trackandtrace.com:/trackandtracechannel.block .

# Step 8
docker cp ./trackandtracechannel.block peer0.consignee.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.precarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.maincarrier.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.oncarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.customs.trackandtrace.com:/trackandtracechannel.block

# Step 11
docker exec -it peer0.consignee.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.precarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.maincarrier.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.oncarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.customs.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Step 12 TODO: Add zip creation of java project
# gradle build
# zip -r code.zip ./build/libs/chaincode-1.0-SNAPSHOT.jar
# peer chaincode install -n mychaincode -v 1.0 -p /path/to/chaincode-directory
