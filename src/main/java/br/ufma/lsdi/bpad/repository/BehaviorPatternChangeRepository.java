package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.AbnormalBehavior;
import br.ufma.lsdi.bpad.model.BehavioralPatternChange;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BehaviorPatternChangeRepository extends MongoRepository<BehavioralPatternChange, String> {

    List<BehavioralPatternChange> findBehavioralPatternChangeByUid(String uid);

    List<BehavioralPatternChange> findBehavioralPatternChangeByUidAndContextAttribute(String uid, String context);

    List<BehavioralPatternChange> findBehavioralPatternChangeByUidAndBehaviorsContaining(String uid, String behavior);

    List<BehavioralPatternChange> findBehavioralPatternChangeByUidAndContextAttributeAndBehaviorsContaining(String uid, String context, String behavior);



}
