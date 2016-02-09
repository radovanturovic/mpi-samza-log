bin/grid stop all
if [ -x deploy/samza ]; then
	echo "Samza direktorijum vec postoji."
	if [ -x deploy/samza/* ]; then
		rm -R deploy/samza/*
		echo "Samza direktorijum je ispraznjen."
	else
		echo "Samza direktorijum je prazan, nastavljam dalje."
	fi
else
	mkdir -p deploy/samza
	echo "Samza direktorijum je napravljen."
fi
tar -zxf target/samza-log-processing-1.0-SNAPSHOT-dist.tar.gz -C deploy/samza -v
bin/grid start all
deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic links-raw

