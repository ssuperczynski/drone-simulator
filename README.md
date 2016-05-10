#Installation:

1. Download Spark (1.6)
2. Compile Spark by typing `mvn clean package`
3. Open terminal with this dir and compile project `./sbt clean compile package`
4. Run Spark examples in the terminal:

```sh
/opt/spark/bin/spark-submit --class CsvParser --master local[4] /Users/sebastian/workshop/target/scala-2.11/espeo-workshops_2.11-1.0.jar
```

where `CsvParser` is your class name.

To run generator just type:

```sh
scala HttpLogGenerator.scala /tmp/apache.txt 100
```
