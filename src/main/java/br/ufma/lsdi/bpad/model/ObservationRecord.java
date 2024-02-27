package br.ufma.lsdi.bpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Data
@Document
@AllArgsConstructor
public class ObservationRecord {

    @Id
    private String id;

    private String uid;

    private String jobkey;

    private String context;

    private List<LocalDate> observationDates;


}
