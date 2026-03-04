# Large-Scale Data Analysis: Weighted Olympic Medal Tally

## Overview
This project processes 120 years of Olympic history (1896-2016) using a custom Hadoop MapReduce job. Instead of a simple medal count, it applies a weighted algorithmic scoring system (Gold = 3 points, Silver = 2 points, Bronze = 1 point) to determine the true all-time historical dominance of each National Olympic Committee (NOC).

## Prerequisites
* Docker & Docker Compose
* Java 8 (JDK 1.8)
* Apache Maven
* `athlete_events.csv` dataset (from Kaggle)

## Execution Steps

1. **Start the Hadoop Cluster:**
   Navigate to the directory containing `docker-compose.yaml` and run:
   ```bash
   docker-compose up -d
   ```

2. **Load Dataset into HDFS:**
Copy the dataset into the NameNode and put it into HDFS:

    ```bash
    docker cp athlete_events.csv namenode:/athlete_events.csv
    docker exec -it namenode hdfs dfs -mkdir -p /input
    docker exec -it namenode hdfs dfs -put /athlete_events.csv /input/
    ```


3. **Compile the Source Code:**
Build the executable JAR file using Maven:
    ```bash
    mvn clean package -DskipTests
    ```

4. **Deploy and Run the MapReduce Job:**
Copy the JAR to the NameNode and execute the job:
    ```bash
    docker cp target/olympic-analyzer-1.0-SNAPSHOT.jar namenode:/job.jar
    docker exec -it namenode hadoop jar /job.jar com.ruhuna.olympics.WeightedMedalTally /input/athlete_events.csv /output_scores
    ```

5. **View the Results:**
Once the job hits 100%, view the final aggregated scores:
    ```bash
    docker exec -it namenode hdfs dfs -cat /output_scores/part-r-00000
    ```
