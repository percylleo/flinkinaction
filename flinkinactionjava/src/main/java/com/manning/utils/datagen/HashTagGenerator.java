package com.manning.utils.datagen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.shaded.com.google.common.base.Throwables;

/**
 * This class will generate data for word count application
 * 
 * @author Sameer
 *
 */
public class HashTagGenerator implements IDataGenerator<String>{
    public static int NO_OF_HOURS_IN_DAY = 24;
    public static int NO_OF_MINS_IN_HOUR = 60;
    private Random randomNumberGenerator = new Random();
    private final SimpleDateFormat inputSDF = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    private Date inputDate;
    private String[] hashtags = { "#Flink", "#Flink", "#Flink", "#Flink", "#Flink",
                                  "#Flink", "#ChicagoFlinkMeetup", "#ChicagoFlinkMeetup",
                                  "#DCFlinkMeetup", "#NYCFlinkMeetup", "#ApacheBeam",
                                  "#ApacheBeam", "#ApacheBeam", "#GoogleDataFlow",
                                  "#GoogleDataFlow" };
    private List<String> data;
    public HashTagGenerator(String defaultDt, Long randomSeed){
        try{
            this.inputDate = inputSDF.parse(defaultDt);
            if(randomSeed!=null){
                this.randomNumberGenerator = new Random(randomSeed);    
            }            
        }catch(Exception ex){
            Throwables.propagate(ex);
        }        
    }
    public HashTagGenerator(String defaultDt){
        this(defaultDt,null);
    }
    public HashTagGenerator(){

    }

    public String[] getHashtags() {
        return hashtags;
    }
    public void setHashtags(String[] hashtags) {
        this.hashtags = hashtags;
    }
    
    @Override
    public void setData(List<String> data) {
        this.data = data;
    }
    /**
     * This class generates word count data for batch implementation of word
     * count return value will have multiple lines each with the following
     * format $TIME,$HASHTAG where $TIME is in the format yyyyMMddHHmm Example
     * line is 201603051315,#DCFlinkMeetup
     * 
     * @return list of input lines
     */
    @Override
    public void generateData() {
        data = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        try {
            String[] allHashTags = HashTagGenerator.getSampleHashTags();
            int noOfHashTags = allHashTags.length;
            for (int i = 0; i < NO_OF_HOURS_IN_DAY; i++) {
                for (int j = 0; j < NO_OF_MINS_IN_HOUR; j++) {//Min 0 to 59
                    int index = this.randomNumberGenerator.nextInt(noOfHashTags);
                    String hashTagForMinuteOfDay = allHashTags[index];
                    cal.setTime(inputDate);
                    cal.set(Calendar.HOUR_OF_DAY, i);
                    cal.set(Calendar.MINUTE, j);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date newDate = cal.getTime();
                    String dtTime = sdf.format(newDate);
                    data.add(dtTime + "," + hashTagForMinuteOfDay);
                }
            }            

        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
    }
    @Override
    public List<String> getData() {
        return this.data;
    }

    public static String[] getSampleHashTags() {
        String[] hashtags = { "#Flink", "#Flink", "#Flink", "#Flink", "#Flink",
                "#Flink", "#ChicagoFlinkMeetup", "#ChicagoFlinkMeetup",
                "#DCFlinkMeetup", "#NYCFlinkMeetup", "#ApacheBeam",
                "#ApacheBeam", "#ApacheBeam", "#GoogleDataFlow",
                "#GoogleDataFlow" };
        return hashtags;
    }

    public static void main(String[] args) throws Exception {
        HashTagGenerator tagGenerator = new HashTagGenerator("20160316",1000l);
        tagGenerator.generateData();
        FileUtils.writeLines(new File("c:/tmp/hashtags.txt"), tagGenerator.getData());
        
    }


}