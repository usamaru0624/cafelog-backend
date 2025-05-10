#!/bin/bash
echo "==== install: placing jar in place ===="
sudo mkdir -p /home/ec2-user/cafelog/target/
sudo rm -f /home/ec2-user/cafelog/target/cafelog-0.0.1-SNAPSHOT.jar
sudo cp /home/ec2-user/cafelog-deploy/cafelog-0.0.1-SNAPSHOT.jar /home/ec2-user/cafelog/target/
echo "==== install: done ===="

