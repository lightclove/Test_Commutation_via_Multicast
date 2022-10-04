package network.testing.udp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AUTHOR : Lobanov F.S.
 * DATE : 15.01.2016
 * TIME : 10:28
 * DESCRIPTION :
 */
public class NetworkTestUtil {
    public static Map<Double, AtomicLong> computeDistribution(Long min, Long max, Integer parts, List<Long> data){
        Map<Double, AtomicLong> distribution = new LinkedHashMap<>();
        Double gap = (Double.valueOf(max) - Double.valueOf(min)) / Double.valueOf(parts);
        Double border = Double.valueOf(min);

        for(int index = 0; index < parts; ++ index){
            distribution.put(border,new AtomicLong(0));
            border += gap;
        }
        distribution.put(Double.valueOf(max), new AtomicLong(0));

        for(Long value : data){
            Map.Entry<Double,AtomicLong> previous = null;

            for(Map.Entry<Double,AtomicLong> current : distribution.entrySet()){
                if(previous != null){
                    Double previousBorder = previous.getKey();
                    Double currentBorder = current.getKey();

                    int lower = Double.compare(value, previousBorder);
                    int higher = Double.compare(value, currentBorder);

                    if(lower > 0 && higher == 0) {
                        current.getValue().incrementAndGet();
                        break;
                    }else if(lower >= 0 && higher < 0) {
                        previous.getValue().incrementAndGet();
                        break;
                    }
                }
                previous = current;
            }
        }
        return distribution;
    }

    public static Long getMicroseconds(){
        return System.nanoTime() / 1000;
    }

    public static StringBuilder generateResult(Long startTime,
                                               Integer iterations,
                                               Integer parts,
                                               List<Long> times,
                                               Long maximumTime,
                                               Long minimumTime) {
        long timeTotal = (getMicroseconds()) - startTime;
        Long summary = 0L;
        for (Long item : times) {
            summary += item;
        }
        Double average = Double.valueOf(summary) / (double) times.size();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"").append("SEND\",\"").append(iterations).append("\"\n");
        stringBuilder.append("\"").append("RECEIVED\",\"").append(times.size()).append("\"\n");
        stringBuilder.append("\"").append("LOST\",\"").append(iterations - times.size()).append("\"\n");
        stringBuilder.append("\"").append("MAX\",\"").append(maximumTime).append("\"\n");
        stringBuilder.append("\"").append("MIN\",\"").append(minimumTime).append("\"\n");
        stringBuilder.append("\"").append("AVERAGE\",\"").append(String.format("%08.3f",average)).append("\"\n");
        stringBuilder.append("\"").append("TIME TOTAL\",\"").append(timeTotal).append("\"\n");

        System.err.println("SEND : " + iterations);
        System.err.println("RECEIVED : " + times.size());
        System.err.println("LOST : " + (iterations - times.size()));
        System.err.println("MAX : " + maximumTime);
        System.err.println("MIN : " + minimumTime);
        System.err.println("AVERAGE : " + String.format("%08.3f",average));
        System.err.println("TIME TOTAL : " + timeTotal);

        Map<Double,AtomicLong> distribution = computeDistribution(minimumTime, maximumTime, parts, times);
        stringBuilder.append("\n\"TIME\";\"MESSAGES\"\n");

        for(Map.Entry<Double,AtomicLong> current : distribution.entrySet()){
            stringBuilder.append("\"").append(String.format("%08.3f",current.getKey()))
                    .append("\";\"").append(current.getValue().get()).append("\"\n");
        }
        return stringBuilder;
    }
}
