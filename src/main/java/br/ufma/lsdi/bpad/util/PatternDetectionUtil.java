package br.ufma.lsdi.bpad.util;

import br.ufma.lsdi.bpad.model.TimeInterval;
import org.apache.spark.sql.Row;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PatternDetectionUtil {


    public static List<TimeInterval> patternExtraction (List<Row> candidateSlots, int numSlots) {

        double interval_th = Double.parseDouble(candidateSlots.get(0).getAs("interval_th").toString());

        List<TimeInterval> intervals = new ArrayList<>();

        //inicializa o pattern auxliar
        int[] patternAux = new int[numSlots+1];

        for (Row candidateSlot : candidateSlots){
            int slot = Integer.parseInt(candidateSlot.getAs("slot").toString());
            patternAux[slot] = Integer.parseInt(candidateSlot.getAs("count").toString());
        }

        List<Integer> interval = new ArrayList<>();

        for (int i = 1; i < patternAux.length; i++ ){
            if (patternAux[i] != 0) {
                interval.add(i);
            }else{
                if (interval.size()>0){
                    // soma dos slots > limiar
                    int sumInterval = getSum(interval,patternAux);
                    if (sumInterval >= interval_th) {
                        //salva o intervalo
                        intervals.add(buildInterval(interval, numSlots));
                    }
                }
                interval = new ArrayList<>();
            }
            if (i==patternAux.length-1){
                int sumInterval = getSum(interval,patternAux);
                if (sumInterval >= interval_th) {
                    intervals.add(buildInterval(interval, numSlots));
                }
            }
        }


        return intervals;

    }


    private static TimeInterval buildInterval(List<Integer> interval, int numSlots) {
        TimeInterval timeInterval = new TimeInterval();
        try {
            LocalTime startTime = SlotUtil.slotToDate(interval.get(0), true, numSlots);
            LocalTime endTime = SlotUtil.slotToDate(interval.get(interval.size()-1), false, numSlots);
        timeInterval = new TimeInterval(startTime.toString(),endTime.toString());
        }catch (Exception e){
            e.printStackTrace();

        }
        return timeInterval; //todo calcular intensidade de rotina
    }


    private static int getSum(List<Integer> interval, int[] patternAux){
        int sum = 0;
        for (Integer slot : interval){
            sum += patternAux[slot];
        }
        return sum;
    }


    public static double calcSimilarity(List<TimeInterval> activePattern, List<TimeInterval> detectedPattern, int numSlots){

        int[] activePatternArray = timeIntervalToList(activePattern, numSlots);
        int[] detectedPatternArray = timeIntervalToList(detectedPattern, numSlots);

        double intersection = 0;
        double union = 0;

        for (int i = 1; i < numSlots+1; i++ ){
            if (activePatternArray[i] != 0 || detectedPatternArray[i] != 0){
                union += 1;
            }
            if (activePatternArray[i] ==1 & detectedPatternArray[i] == 1){
                intersection += 1;
            }
        }

        return intersection/union;
    }

    private static int[] timeIntervalToList(List<TimeInterval> timeIntervals, int numSlots){
        int[] patternArray = new int[numSlots+1];

        for (TimeInterval timeInterval : timeIntervals){
            for (Integer slot : timeInterval.getSlots(numSlots)){
                patternArray[slot] = 1;
            }
        }
        return patternArray;
    }
}
