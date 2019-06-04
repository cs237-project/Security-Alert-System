cd ~/kafka*
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic topic1
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic topic2
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic topic3
