# SAMZA

Svu potrebnu dokumentaciju možete pronaći na: http://samza.apache.org/

## Instalacija

Za rad sa Samza-om potrebno je imati:
* Java 1.7+ (u ovom primeru se koristi Java 1.8) [java_1.8_install](http://tecadmin.net/install-oracle-java-8-jdk-8-ubuntu-via-ppa/)
* Maven 3 [maven_3_install](http://stackoverflow.com/questions/15630055/how-to-install-maven-3-on-ubuntu-15-04-14-10-14-04-lts-13-10-13-04-12-10-12-04-b)
* python-dev (sudo apt-get install python-dev)
* scala 2.7+

Prilikom pokretanja primera, samostalno će se instalirati:
* Zookeeper
* Kafka
* Yarn

## Pokretanje

### Preuzeti primer sa: 
```
git clone https://github.com/aleksandarbirca/mpi-samza-log.git
cd mpi-samza-log
```

### Buildovati kod:
```
mvn clean package
```

### Pokrenuti bootstrap skriptu:
```
bin/grid bootstrap
mkdir -p deploy/samza
tar -xvf target/--releaseName.tar -C deploy/samza
```

### Pokrenuti poslove:
```
deploy/samza/bin/run-job.sh --config-factory=fullPackagePathTo.PropertiesConfigFactory --config-path=file://$PWD/pathToProperties/propertiesfile.properties 
```
Konkretno za DataStreamTask:
```
deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/data-stream.properties
```

### Pregled pristiglih poruka u topic:
```
deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:<port> --topic <topic-name>
```
Konkretno za DataStreamTask:
```
deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic links-raw
```
