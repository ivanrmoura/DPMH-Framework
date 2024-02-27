package br.ufma.lsdi.bpad.controller;

import br.ufma.lsdi.bpad.model.BehavioralPattern;
import br.ufma.lsdi.bpad.repository.BehavioralPatternRepository;
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

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/behaviorpattern")
@RestController
public class BehaviorPatternController {

    @Autowired
    private BehavioralPatternRepository patternRepository;

    @ApiOperation(value = "Get behavioral patterns considering a user identifier.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavior patterns "),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })
    @GetMapping("uid/{uid}")
    private ResponseEntity<List<BehavioralPattern>> getPatternByUid(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUid(uid);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral patterns considering a user identifier and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavior patterns "),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-behavior/{uid}/{behavior}")
    private ResponseEntity<List<BehavioralPattern>> getPatternByUidAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndBehaviorsContaining(uid, behavior);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral patterns considering a user identifier and a context attribute.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavior patterns "),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context/{uid}/{context}")
    private ResponseEntity<List<BehavioralPattern>> getPatternByUidAndContext(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndContextAttribute(uid, context);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get behavioral patterns considering a user identifier, a context attribute, and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of behavior patterns "),
            @ApiResponse(code= 204, message = "No pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("uid-context-behavior/{uid}/{context}/{behavior}")
    private ResponseEntity<List<BehavioralPattern>> getPatternByUidAndContextAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndContextAttributeAndBehaviorsContaining(uid, context, behavior);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }


    // endpoits current patterns

    @ApiOperation(value = "Get current behavioral patterns considering a user identifier.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of current behavior patterns "),
            @ApiResponse(code= 204, message = "No current behavior pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/currentpattern/uid/{uid}")
    private ResponseEntity<List<BehavioralPattern>> getCurretntPatternByUid(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndCurrentPattern(uid, true);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get current behavioral patterns considering a user identifier and a context attribute.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of current behavior patterns "),
            @ApiResponse(code= 204, message = "No current behavior pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/currentpattern/uid-context/{uid}/{context}")
    private ResponseEntity<List<BehavioralPattern>> getCurretntPatternByUidAndContext(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndContextAttributeAndCurrentPattern(uid, context, true);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get current behavioral patterns considering a user identifier and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of current behavior patterns "),
            @ApiResponse(code= 204, message = "No current behavior pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/currentpattern/uid-behavior/{uid}/{behavior}")
    private ResponseEntity<List<BehavioralPattern>> getCurretntPatternByUidAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndBehaviorsContainingAndCurrentPattern(uid, behavior, true);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }

    @ApiOperation(value = "Get current behavioral patterns considering a user identifier, a context attribute, and a behavior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of current behavior patterns "),
            @ApiResponse(code= 204, message = "No current behavior pattern found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/currentpattern/uid-context-behavior/{uid}/{context}/{behavior}")
    private ResponseEntity<List<BehavioralPattern>> getCurretntPatternByUidAndBehavior(
            @ApiParam(name = "uid", value = "user identifier", example = "ivan.rodrigues@lsdi.ufma.br")
            @PathVariable String uid,
            @ApiParam(name = "context", value = "context attribute", example = "MONDAY")
            @PathVariable String context,
            @ApiParam(name = "behavior", value = "human behavior", example = "sociability")
            @PathVariable String behavior
    ){
        List<BehavioralPattern> patterns = patternRepository
                .findBehavioralPatternByUidAndContextAttributeAndBehaviorsContainingAndCurrentPattern(uid, context, behavior, true);

        if (patterns.size() > 0){
            return ResponseEntity.ok(patterns);
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(patterns);
        }
    }


}
