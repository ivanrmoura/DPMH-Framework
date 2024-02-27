package br.ufma.lsdi.bpad.controller;

import br.ufma.lsdi.bpad.model.BehavioralPatternChange;
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

@RequestMapping("/patternchange")
@RestController
public class BehaviorPatternChangeController {

    @Autowired
    private BehaviorPatternChangeRepository patternChangeRepository;


    @ApiOperation(value = "Get behavioral pattern changes considering a user identifier.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavioral pattern changes"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })
    @GetMapping("uid/{uid}")
    private ResponseEntity<List<BehavioralPatternChange>> getPatternChangeByUid(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid
    ){
        List<BehavioralPatternChange> patterns = patternChangeRepository
                .findBehavioralPatternChangeByUid(uid);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral pattern changes considering a user identifier and a context attribute.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavioral pattern changes"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context/{uid}/{context}")
    private ResponseEntity<List<BehavioralPatternChange>> getPatternChangeByUidAndContext(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context
    ){
        List<BehavioralPatternChange> patterns = patternChangeRepository
                .findBehavioralPatternChangeByUidAndContextAttribute(uid, context);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral pattern changes considering a user identifier and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavioral pattern changes"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-behavior/{uid}/{behavior}")
    private ResponseEntity<List<BehavioralPatternChange>> getPatternChangeByUidAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPatternChange> patterns = patternChangeRepository
                .findBehavioralPatternChangeByUidAndBehaviorsContaining(uid, behavior);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral pattern changes considering a user identifier, a context attribute, and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavioral pattern changes"),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context-behavior/{uid}/{context}/{behavior}")
    private ResponseEntity<List<BehavioralPatternChange>> getPatternChangeByUidAndContextAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPatternChange> patterns = patternChangeRepository
                .findBehavioralPatternChangeByUidAndContextAttributeAndBehaviorsContaining(uid, context, behavior);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

}
