package org.ugizashinje;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.util.Arrays;

import static java.util.Arrays.*;

class SpeedMeter implements Runnable{

    public static   final long TIME = 1000L;
    public  long     counter = 0;
    private long    counterAtStart;
    private long    counterAtEnd;
    public  boolean measuringFinished = false;
    private long    start;
    private long    end;
    @Override
    public void run() {
        try {
            while (true) {
                start = System.nanoTime();
                counterAtStart = counter;
                Thread.sleep(TIME);
                end = System.nanoTime();
                counterAtEnd = counter;
                long currentInterval = counterAtEnd - counterAtStart ;
                double speed = currentInterval * 1000.0 / (TIME);
                System.out.print("entrys " + currentInterval + "  total: " + counter +"                \r");
                if (measuringFinished) {
                    return;
                }
            }
        } catch (Exception e){
            System.out.println();
        }
    }
}

public class HazlecastClientDemo {


    private static final ILogger LOGGER = Logger.getLogger(HazlecastClientDemo.class);

    public static final long  NUMBER_OF_ENTRYS = 1000_000L;
    public static final int   SIZE_OF_ENTRY = 1000;
    public static final String ENTRY = createEntry();

    private static String createEntry() {
        char buff[] = new char[SIZE_OF_ENTRY];
        Arrays.asList(buff,'A');
        return new String(buff);
    }

    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = new ClientConfig().addAddress("127.0.0.1");
        HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<Long,String> map = hzClient.getMap("some");
        System.out.println("Started loading data ");
        SpeedMeter meter = new SpeedMeter();
        new Thread(meter).start();
        for (long i=0 ; i <  NUMBER_OF_ENTRYS ; i ++ ) {
            map.put(new Long(i), ENTRY);
            meter.counter++ ;
        }
        System.out.println("Inserted 10 000 000 elements");


    }
}
