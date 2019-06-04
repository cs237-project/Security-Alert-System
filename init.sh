cd ~
cd zookeeper*
./bin/zkServer.sh start
echo "start Zookeeper"
cd ~
cd kafka*
nohup ./bin/kafka-server-start.sh ./config/server.properties > kafka.log &
echo "start Kafka"
cd ~
cd apache-activemq*
./bin/activemq start
echo "start ActiveMQ, go to localhost:8161/admin/"
cd ~
sudo service rabbitmq-server start
echo "start Rabbitmq, go to localhost:15672"
