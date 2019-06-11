cd ~/kafka*
./bin/kafka-server-stop.sh
echo "stop Kafka"
cd ~/zookeeper*
./bin/zkServer.sh stop
cd ~/apache-active*
./bin/activemq stop
echo "stop ActiveMQ"
sudo rabbitmqctl stop
echo "stop RabbitMQ"
