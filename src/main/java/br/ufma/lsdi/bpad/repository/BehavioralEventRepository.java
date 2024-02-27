package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.BehavioralEvent;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface BehavioralEventRepository extends MongoRepository<BehavioralEvent, String> {


    @Aggregation(pipeline = {
            "{'$match': {'uid' : ?0}}",
            "{ '$addFields': { 'extracted_date': { $dateToString: { format: '%Y-%m-%d', date: '$timestamp', timezone:'America/Sao_Paulo'} } }  }",
           // "{'$addFields': { 'extracted_date': {'$dateFromParts': { 'year': {'$year': '$timestamp'}, 'month': {'$month': '$timestamp'}, 'day': {'$dayOfMonth': '$timestamp'} }} }}",
            "{'$match': { '$and' : [   {'extracted_date' : {'$eq' :   ?1} }]}}",
            "{ $unwind : '$contexts' }",
            "{ $group: { '_id': null, 'uniqueArray': { '$addToSet': '$contexts' } }}",
            "{$project: { 'uniqueArray' : 1, '_id' : 0}}"
    })
    public String selectContextsOfDay(String uid, String date);



}
