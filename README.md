# Security-Alert-System
Security Alert System based on Pub/Sub Middleware Environment

1. create message

2. create queues

3. send messages

4. read messages

5. read test result

When using kafka, we should purge all the queues by deleting all the existing topic before test.

bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic "topic_name"

### Todo:
1. Need to build a front-end test environment
2. Need to fix the problem of kafka which does not consume the message in queue.
 
