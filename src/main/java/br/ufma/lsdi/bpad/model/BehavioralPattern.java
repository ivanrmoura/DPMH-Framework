package br.ufma.lsdi.bpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehavioralPattern {

    @Id
    private String id;

    private boolean currentPattern;

    private String jobKey;

    private String uid;

    private LocalDate detectionDate;

    private List<String> behaviors;

    private String contextAttribute;

    private List<TimeInterval> dailyRoutine;

    private int numSlots;

    public List<List<String>> getTimeIntervalList(){
        List<List<String>> intervals = new ArrayList<>();
        for (TimeInterval timeInterval : this.dailyRoutine){
            List<String> interval = Arrays.asList(timeInterval.getStartTime(), timeInterval.getEndTime());
            intervals.add(interval);
        }
        return intervals;
    }


}
