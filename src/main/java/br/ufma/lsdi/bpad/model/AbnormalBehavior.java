package br.ufma.lsdi.bpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Document
@Data
@AllArgsConstructor
public class AbnormalBehavior {

    @Id
    private String id;

    private String jobKey;

    private String uid;

    private LocalDate detectionDate;

    private List<String> behaviors;

    private String contextAttribute;

    private Double similarity;

    private Double defuzzifiedValue;

    private Double relevance;

    private List<TimeInterval> dailyRoutine;

    private List<TimeInterval> abnormalDailyRoutine;

}
