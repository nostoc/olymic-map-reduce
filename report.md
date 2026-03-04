**1. Approach & Methodology**

The objective of this task was to evaluate the historical dominance of countries in the Olympic Games. A dataset containing over 270,000 historical athlete records from 1896 to 2016 was selected from Kaggle. To extract deeper insights beyond basic medal counts, a custom MapReduce job was implemented in Java to calculate a "Weighted Medal Tally." 

* **Mapper Logic:** The Mapper parses the raw CSV file line by line. It extracts the National Olympic Committee (NOC) code and the medal won. It then applies a weighted scoring algorithm: assigning 3 points for Gold, 2 points for Silver, and 1 point for Bronze. Non-medalists are filtered out.
* **Reducer Logic:** The Reducer aggregates the intermediate key-value pairs (NOC, Points) and sums the total weighted score for each country across the 120-year period.
* **Environment:** The job was executed on a locally hosted, single-node Apache Hadoop cluster (version 3.2.1) utilizing Docker Compose to manage the NameNode, DataNode, and YARN ResourceManager containers.

**2. Results Summary & Insights**

The MapReduce job successfully processed the full dataset and output the aggregated historical scores for all participating NOCs. A significant insight derived from the data is the overwhelming historical lead of the USA, which accumulated a total of 12,554 points. 

Furthermore, the data explicitly highlights the geopolitical shifts of the 20th century. The Soviet Union (URS) ranks as the second most dominant Olympic entity of all time with 5,399 points, and East Germany (GDR) ranks highly with 2,126 points, despite both nations being dissolved decades ago. This pattern reveals that Cold War-era state-sponsored athletic programs generated a volume of medals that modern nations (like China, currently at 2,036 points) are only just beginning to rival. 

**3. Performance and Accuracy Observations**
* **Accuracy:** A standard comma-delimiter split would have caused severe data corruption due to embedded commas within athlete names in the CSV. To ensure 100% parsing accuracy, a custom regular expression `split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")` was utilized in the Mapper to safely bypass commas enclosed in quotation marks. 
* **Performance:** To optimize network I/O and reduce the data transfer load between the Map and Reduce phases, the Reducer class was also assigned as a Combiner `job.setCombinerClass(ScoreReducer.class)`. This allowed local pre-aggregation of the NOC scores on the Map nodes before shuffling the data across the network.
* **Model Expansion:** This model could be expanded by chaining a second MapReduce job that integrates a secondary dataset containing historical national populations. This would allow the system to calculate a "Weighted Score Per Capita," revealing which geographically smaller nations are actually the most efficient at producing elite athletes.

***