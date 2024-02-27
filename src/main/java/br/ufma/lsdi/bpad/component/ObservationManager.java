package br.ufma.lsdi.bpad.component;

import br.ufma.lsdi.bpad.model.ObservationRecord;
import br.ufma.lsdi.bpad.repository.BehavioralEventRepository;
import br.ufma.lsdi.bpad.repository.ObservationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObservationManager {

    @Autowired
    private BehavioralEventRepository phenotypeEventRepository;

    @Autowired
    private ObservationRecordRepository observationRecordRepository;

    public String selectContextsOfObservation(String uid, LocalDate date){
        return phenotypeEventRepository.selectContextsOfDay(uid, date.toString());
    }

    public ObservationRecord updateObservationRecorde(String uid, String jobKey, String context, LocalDate date){

        ObservationRecord observationRecord = observationRecordRepository
                .findObservationRecordByJobkeyAndContext(jobKey, context).orElse(new ObservationRecord(
                        null, uid, jobKey, context, new ArrayList<>()));

        observationRecord.getObservationDates().add(date);
        observationRecordRepository.save(observationRecord);
        return observationRecord;
    }


    public void cleanObservationRecord(ObservationRecord observationRecord){
        observationRecord.setObservationDates(new ArrayList<>());
        observationRecordRepository.save(observationRecord);

    }


}
