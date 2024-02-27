package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.AbnormalBehavior;
import br.ufma.lsdi.bpad.model.BehavioralPattern;
import br.ufma.lsdi.bpad.model.BehavioralPatternChange;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AbnormalBehaviorRepository extends MongoRepository<AbnormalBehavior, String> {


    List<AbnormalBehavior> findAbnormalBehaviorByUid(String uid);

    List<AbnormalBehavior> findAbnormalBehaviorByUidAndContextAttribute(String uid, String context);

    List<AbnormalBehavior> findAbnormalBehaviorByUidAndBehaviorsContaining(String uid, String behavior);

    List<AbnormalBehavior> findAbnormalBehaviorByUidAndContextAttributeAndBehaviorsContaining(String uid, String context, String behavior);



}
