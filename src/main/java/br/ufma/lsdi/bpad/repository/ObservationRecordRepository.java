package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.BehavioralPattern;
import br.ufma.lsdi.bpad.model.ObservationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ObservationRecordRepository extends MongoRepository<ObservationRecord, String> {

    public Optional<ObservationRecord> findObservationRecordByJobkeyAndContext(String jobKey, String context);

}
