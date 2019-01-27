start:
	docker-compose up --build &> log &
	sleep 10
	docker exec kafkastreams_kafka_1 connect-standalone /usr/share/logs/my-standalone.properties /usr/share/logs/connect-file-source.properties

clean:
	docker-compose stop; docker-compose down; docker-compose rm;