# Setze Umgebungsvariablen
export FABRIC_CFG_PATH=/Users/renato.mameli/Hyperledger/trackandtrace
export PATH=~/Hyperledger/fabric-samples/bin:$PATH

# Schritt 0: Fahre laufende Container runter und lösche generierte Konfigurationsdateien
docker compose down
rm -r channel-artifacts
rm -r crypto-config
rm -r system-genesis-block

# Schritt 1: Generiere crypto-configs
cryptogen generate --config=./crypto-config.yaml

# Schritt 2: Generiere Genesis Block
configtxgen -profile SampleMultiNodeEtcdRaft -channelID system-channel -outputBlock ./system-genesis-block/genesis.block

# Schritt 3: Erstelle Channel-Artifakte
configtxgen -profile Frachtverfolgung -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID trackandtracechannel

# Schritt 4: Starte Netzwerk mit Docker
docker-compose up -d

# Warte bis die Docker-Container hochgefahren sind
sleep 5

# Schritt 5: Erstelle einen Channel vom Peer Container aus
docker exec -it peer0.consignor.trackandtrace.com peer channel create -o orderer.trackandtrace.com:7050 -c trackandtracechannel -f /etc/hyperledger/fabric/channel-artifacts/channel.tx --tls true --cafile /etc/hyperledger/fabric/orderer/tlsca.trackandtrace.com-cert.pem

# Schritt 6: Trete dem Channel mit dem ersten Peer bei
docker exec -it peer0.consignor.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Schritt 7: Kopiere den Channel-Block auf das lokale System
docker cp peer0.consignor.trackandtrace.com:/trackandtracechannel.block .

# Schritt 8. Kopiere den Block vom lokalen System auf die restlichen Peers.
docker cp ./trackandtracechannel.block peer0.consignee.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.precarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.maincarrier.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.oncarriageshipper.trackandtrace.com:/trackandtracechannel.block
docker cp ./trackandtracechannel.block peer0.customs.trackandtrace.com:/trackandtracechannel.block

# Schritt 9: Die restlichen Peers treten dem Channel bei
docker exec -it peer0.consignee.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.precarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.maincarrier.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.oncarriageshipper.trackandtrace.com peer channel join -b /trackandtracechannel.block
docker exec -it peer0.customs.trackandtrace.com peer channel join -b /trackandtracechannel.block

# Schritt 10: Verpacke and installiere den Chaincode
# docker exec -it peer0.consignor.trackandtrace.com peer lifecycle chaincode package mychaincode.tar.gz --path /etc/hyperledger/fabric/chaincode/ --lang java --label mychaincode_1

# docker exec -it peer0.consignor.trackandtrace.com peer lifecycle chaincode install mychaincode.tar.gz
