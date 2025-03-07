# kotlin-spring-snippet
kotlin-spring-snippet

## kafka
- ```bash
  # hello topic 생성, 파티션 4
  kafka-topics.sh --create \
  --bootstrap-server kafka:9092 \
  --topic hello \
  --partitions 4 \
  --replication-factor 1 \
  --config retention.ms=600000 \
  --config segment.ms=600000 \
  --config delete.retention.ms=120000 \
  --config compression.type=zstd \
  --config min.insync.replicas=1 \
  --config max.message.bytes=262144 \
  --config flush.messages=1 \
  --config flush.ms=1000
  
  # 컨슈머 그룹 목록
  kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
  
  # 컨슈머 그룹 group1의 상태 보기(파티션, 오프셋, 랙, 호스트 확인 가능)
  kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group group1 --describe
  
  ```