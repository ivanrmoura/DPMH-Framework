package br.ufma.lsdi.bpad.controller;

import br.ufma.lsdi.bpad.model.AbnormalBehavior;
import br.ufma.lsdi.bpad.model.BehavioralPatternChange;
import br.ufma.lsdi.bpad.repository.AbnormalBehaviorRepository;
import br.ufma.lsdi.bpad.repository.BehaviorPatternChangeRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/abnormalbehavior")
@RestController
public class AbnormalBehaviorController {

    @Autowired
    private AbnormalBehaviorRepository abnormalBehaviorRepository;


    @ApiOperation(value = "Get abnormal behaviors considering a user identifier.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of babnormal behaviors"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })
    @GetMapping("uid/{uid}")
    private ResponseEntity<List<AbnormalBehavior>> getAbnormalBehaviorByUid(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid
    ){
        List<AbnormalBehavior> abnormalBehaviors = abnormalBehaviorRepository
                .findAbnormalBehaviorByUid(uid);

        if (abnormalBehaviors.size() > 0){
            return ResponseEntity.ok(abnormalBehaviors);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(abnormalBehaviors);
        }
    }

    @ApiOperation(value = "Get abnormal behaviors considering a user identifier and a context attribute.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of abnormal behaviors"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context/{uid}/{context}")
    private ResponseEntity<List<AbnormalBehavior>> getAbnormalBehaviorByUidAndContext(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context
    ){
        List<AbnormalBehavior> abnormalBehaviors = abnormalBehaviorRepository
                .findAbnormalBehaviorByUidAndContextAttribute(uid, context);

        if (abnormalBehaviors.size() > 0){
            return ResponseEntity.ok(abnormalBehaviors);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(abnormalBehaviors);
        }
    }

    @ApiOperation(value = "Get abnormal behaviors considering a user identifier and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of abnormal behaviors"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-behavior/{uid}/{behavior}")
    private ResponseEntity<List<AbnormalBehavior>> getAbnormalBehaviorByUidAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<AbnormalBehavior> abnormalBehaviors = abnormalBehaviorRepository
                .findAbnormalBehaviorByUidAndBehaviorsContaining(uid, behavior);

        if (abnormalBehaviors.size() > 0){
            return ResponseEntity.ok(abnormalBehaviors);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(abnormalBehaviors);
        }
    }

    @ApiOperation(value = "Get abnormal behaviors considering a user identifier, a context attribute, and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of abnormal behaviors"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context-behavior/{uid}/{context}/{behavior}")
    private ResponseEntity<List<AbnormalBehavior>> getPatternChangeByUidAndContextAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<AbnormalBehavior> abnormalBehaviors = abnormalBehaviorRepository
                .findAbnormalBehaviorByUidAndContextAttributeAndBehaviorsContaining(uid, context, behavior);

        if (abnormalBehaviors.size() > 0){
            return ResponseEntity.ok(abnormalBehaviors);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(abnormalBehaviors);
        }
    }

}
