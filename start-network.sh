# Set Environment variables
export FABRIC_CFG_PATH=/Users/renato.mameli/Hyperledger/trackandtrace
export PATH=~/Hyperledger/fabric-samples/bin:$PATH

# Step 0: Shut down running containers and delete generated configuration files
docker compose down
rm -r channel-artifacts
rm -r crypto-config
rm -r system-genesis-block


# Step 1: Generate crypto-configs
cryptogen generate --config=./crypto-config.yaml

# Step 2: Generate Genesis Block
configtxgen -profile SampleMultiNodeEtcdRaft -channelID system-channel -outputBlock ./system-genesis-block/genesis.block

# Step 3: Create channel artifacts
configtxgen -profile Frachtverfolgung -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID trackandtracechannel

# Step 4: Start network in socker
docker-compose up -d

# Wait until docker containers have started
sleep 5

# Step 5: Create a channel out of the peer container
docker exec -it peer0.consignor.trackandtrace.com peer channel create -o orderer.trackandtrace.com:7050 -c trackandtracechannel -f /etc/hyperledger/fabric/channel-artifacts/channel.tx --tls true --cafile /etc/hyperledger/fabric/orderer/tlsca.trackandtrace.com-cert.pem

# Step 6: Join the channel with the first peer
docker exec -it peer0.consignor.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Step 7: Copy the channel block to the local system
docker cp peer0.consignor.trackandtrace.com:/trackandtracechannel.block .

# Step 8. Copy the block at the local system to all other peers
docker cp ./trackandtracechannel.block peer0.consignee.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.precarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.maincarrier.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.oncarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.customs.trackandtrace.com:/trackandtracechannel.block

# Step 11: Join the channel from all other peers
docker exec -it peer0.consignee.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.precarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.maincarrier.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.oncarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.customs.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Step 12: Package and install chaincode
#docker exec -it peer0.consignor.trackandtrace.com peer lifecycle chaincode package mychaincode.tar.gz --path /etc/hyperledger/fabric/chaincode/ --lang java --label mychaincode_1

# docker exec -it peer0.consignor.trackandtrace.com peer lifecycle chaincode install mychaincode.tar.gz
