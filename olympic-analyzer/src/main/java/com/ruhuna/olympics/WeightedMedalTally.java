package com.ruhuna.olympics;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WeightedMedalTally {

    // MAPPER: Extracts the NOC and assigns a weighted score based on the medal
    public static class MedalMapper extends Mapper<Object, Text, Text, IntWritable> {
        private Text noc = new Text();
        private IntWritable score = new IntWritable();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            // Skip the CSV header row
            if (line.startsWith("ID"))
                return;

            // Standard CSV split (Dataset columns: 7 is NOC, 14 is Medal)
            // Using a regex split to safely ignore commas inside quotes
            String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if (columns.length > 14) {
                String medalType = columns[14].replace("\"", "").trim();
                String nocStr = columns[7].replace("\"", "").trim();

                int points = 0;
                if (medalType.equals("Gold")) {
                    points = 3;
                } else if (medalType.equals("Silver")) {
                    points = 2;
                } else if (medalType.equals("Bronze")) {
                    points = 1;
                }

                // Only emit if they actually won a medal
                if (points > 0) {
                    noc.set(nocStr);
                    score.set(points);
                    context.write(noc, score);
                }
            }
        }
    }

    // REDUCER: Sums up all the weighted points for each NOC
    public static class ScoreReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    // DRIVER: Configures and launches the job
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Weighted Medal Tally");

        job.setJarByClass(WeightedMedalTally.class);
        job.setMapperClass(MedalMapper.class);
        job.setCombinerClass(ScoreReducer.class); // Using reducer as combiner for network efficiency
        job.setReducerClass(ScoreReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}