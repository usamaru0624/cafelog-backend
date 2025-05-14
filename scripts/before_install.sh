#!/bin/bash
echo "==== before_install: cleaning deploy directory ===="
sudo rm -f /home/ec2-user/cafelog-deploy/cafelog-0.0.1-SNAPSHOT.jar
sudo chown -R ec2-user:ec2-user /home/ec2-user/cafelog-deploy
