# Security-Alert-System
Security Alert System based on Pub/Sub Middleware Environment

1. create message

2. create queues

3. send messages

4. read messages

5. read test result

Start all the message queue framework run:

bash init.sh

Stop all the message queue framework run:

bash stop.sh

When using kafka, we should purge all the queues by deleting all the existing topic before test.

bash purge.sh
