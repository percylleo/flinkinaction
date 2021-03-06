package com.manning.fia.c04;

import com.manning.fia.transformations.media.ComputeTimeSpentPerSectionAndSubSection;
import com.manning.fia.transformations.media.NewsFeedMapper;
import com.manning.fia.utils.NewsFeedDataSource;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * this example basically does a simple streaming i.e grouping the data keys and
 * doing a sum aggregation cummulatively as & when the data arrives. no concept
 * of Windows as it is basically KeyedStream
 * if it is kafka
 * --isKafka true --topic newsfeed --bootstrap.servers localhost:9092 --num-partions 10 --zookeeper.connect
 * localhost:2181 --group.id myconsumer --parallelism numberofpartions
 * else
 * don't need to send anything.
 * one of the optional parameters for both the sections are
 * --fileName /media/pipe/newsfeed
 */
public class SimpleStreamingExample2 {

    private void executeJob(ParameterTool parameterTool) throws Exception {

        StreamExecutionEnvironment execEnv;
        DataStream<String> dataStream;
        DataStream<Tuple3<String, String, Long>> selectDS;
        KeyedStream<Tuple3<String, String, Long>, Tuple> keyedDS;
        DataStream<Tuple3<String, String, Long>> result ;
        
        execEnv = StreamExecutionEnvironment.getExecutionEnvironment();

        execEnv.setParallelism(parameterTool.getInt("parallelism", 1));

        boolean isKafka = parameterTool.getBoolean("isKafka", false);
        if (isKafka) {
            dataStream = execEnv.addSource(NewsFeedDataSource.getKafkaDataSource(parameterTool));
        } else {
            dataStream = execEnv.addSource(NewsFeedDataSource.getCustomDataSource(parameterTool));
        }

        
        selectDS = dataStream.map(new NewsFeedMapper()).project(1, 2, 4);
        
        keyedDS = selectDS.keyBy(0, 1);
        
        result = keyedDS.reduce(new ComputeTimeSpentPerSectionAndSubSection());

        result.print();

        execEnv.execute("Simple Streaming");
    }

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        SimpleStreamingExample2 window = new SimpleStreamingExample2();
        window.executeJob(parameterTool);
    }
}
