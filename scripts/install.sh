#!/bin/bash
echo "==== install: placing jar in place ===="

# 1. デプロイ先ディレクトリを確実に作成
mkdir -p /home/ec2-user/cafelog/target/

# 2. 古い jar を削除（必要に応じて）
rm -f /home/ec2-user/cafelog/target/cafelog-0.0.1-SNAPSHOT.jar

# 3. deployフォルダから target にコピー
cp /home/ec2-user/cafelog-deploy/cafelog-0.0.1-SNAPSHOT.jar /home/ec2-user/cafelog/target/

echo "==== install: done ===="

