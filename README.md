# Large-Scale Data Analysis: Weighted Olympic Medal Tally

## Overview
This project analyzes 120 years of Olympic Games history (1896-2016) using distributed computing technology. Rather than simply counting medals, we use a points-based scoring system where Gold medals earn 3 points, Silver medals earn 2 points, and Bronze medals earn 1 point. This method provides a more accurate picture of each country's overall Olympic performance throughout history.

The analysis is performed using Hadoop MapReduce, a technology designed to process large datasets by dividing the work across multiple computers working in parallel.

## Prerequisites
* Docker & Docker Compose (software that creates isolated computing environments)
* Java 8 (JDK 1.8) (programming language runtime)
* Apache Maven (build automation tool)
* `athlete_events.csv` dataset (historical Olympic data from [Kaggle](https://www.kaggle.com/datasets/heesoo37/120-years-of-olympic-history-athletes-and-results))

## Step-by-Step Instructions

### 1. Clone the Repository
Get the project code from GitHub ([nostoc/olymic-map-reduce](https://github.com/nostoc/olymic-map-reduce)):
```bash
git clone https://github.com/nostoc/olymic-map-reduce
cd olymic-map-reduce
```

### 2. Start the Hadoop Cluster
Navigate to the folder containing `docker-compose.yaml` and execute:
```bash
docker-compose up -d
```
This command creates and starts a distributed computing environment on your machine.

### 3. Upload Dataset to the Distributed File System
Transfer the Olympic history dataset into the system:

```bash
docker cp athlete_events.csv namenode:/athlete_events.csv
docker exec -it namenode hdfs dfs -mkdir -p /input
docker exec -it namenode hdfs dfs -put /athlete_events.csv /input/
```

These commands copy the data file into the Hadoop Distributed File System (HDFS), where it can be accessed by the analysis program.

### 4. Compile the Analysis Program
Build the executable program file using Maven:
```bash
mvn clean package -DskipTests
```
This creates a JAR file (a packaged Java application) ready for execution.

### 5. Deploy and Execute the Analysis
Copy the program to the Hadoop cluster and run the analysis:
```bash
docker cp target/olympic-analyzer-1.0-SNAPSHOT.jar namenode:/job.jar
docker exec -it namenode hadoop jar /job.jar com.ruhuna.olympics.WeightedMedalTally /input/athlete_events.csv /output_scores
```

The program will process all 120 years of data, calculating weighted scores for each country.

### 6. View the Results
After the analysis completes (progress reaches 100%), retrieve the final country rankings:
```bash
docker exec -it namenode hdfs dfs -cat /output_scores/part-r-00000
```

This displays each country's total weighted score.