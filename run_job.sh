mvn clean package -Dmaven.test.skip=true;
java -jar ./target/Spring-Batch-Configuration-0.0.1-SNAPSHOT.jar "cost=10VND" "run.date(date)=2023/10/15";
read;