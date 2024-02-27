package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.BehavioralPattern;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BehavioralPatternRepository extends MongoRepository<BehavioralPattern, String> {

    @Aggregation(pipeline = {
            "{'$match': { $and : [{'uid' : ?1 }, " +
                    "{'contextAttribute' : ?2}, " +
                    "{'jobKey' : ?0}, " +
                    "{'currentPattern' : true}]}}",
    })
    public BehavioralPattern findCurrentBehavioralPattern(String jobkey, String uid, String context);

    // find patterns

    public List<BehavioralPattern> findBehavioralPatternByUid(String uid);

    public List<BehavioralPattern> findBehavioralPatternByUidAndContextAttribute(String uid, String context);

    public List<BehavioralPattern> findBehavioralPatternByUidAndBehaviorsContaining(String uid, String behavior);

    public List<BehavioralPattern> findBehavioralPatternByUidAndContextAttributeAndBehaviorsContaining(String uid, String context, String behavior);

    // find current patterns

    public List<BehavioralPattern> findBehavioralPatternByUidAndCurrentPattern(String uid, boolean isCurrentPattern);

    public List<BehavioralPattern> findBehavioralPatternByUidAndContextAttributeAndCurrentPattern(
            String uid, String context, boolean currentPattern);

    public List<BehavioralPattern> findBehavioralPatternByUidAndBehaviorsContainingAndCurrentPattern(
            String uid, String behavior, boolean currentPattern);

    public List<BehavioralPattern> findBehavioralPatternByUidAndContextAttributeAndBehaviorsContainingAndCurrentPattern(
            String uid, String context, String behavior, boolean currentPattern);

}
