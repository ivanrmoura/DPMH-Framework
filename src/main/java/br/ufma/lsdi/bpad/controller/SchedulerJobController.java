package br.ufma.lsdi.bpad.controller;

import br.ufma.lsdi.bpad.job.JobExecutor;
import br.ufma.lsdi.bpad.model.JobData;
import br.ufma.lsdi.bpad.model.SchedulerJobInfo;
import br.ufma.lsdi.bpad.repository.SchedulerJobInfoRepository;
import br.ufma.lsdi.bpad.service.SchedulerJobService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/scheduler")
@RestController
public class SchedulerJobController {

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private SchedulerJobInfoRepository jobInfoRepository;


    @ApiOperation(value = "Get all scheduler job.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return a list of scheduler job"),
            @ApiResponse(code= 204, message = "No scheduler job found"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/getall")
    private ResponseEntity<List<SchedulerJobInfo>> getAllSchendulerJob(){
        List<SchedulerJobInfo> jobInfos = jobInfoRepository.findAll();
            return ResponseEntity.ok(jobInfos);
    }

    @ApiOperation(value = "Create a scheduler job.")
    @ApiResponses(value = {
            @ApiResponse(code= 201, message = "Scheduler job created"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @PostMapping("/create")
    public ResponseEntity<String> createSchendulerJob(
            @ApiParam(name = "jobData", value = "job data")
            @RequestBody JobData jobData
    ){

        SchedulerJobInfo schedulerJobInfo = new SchedulerJobInfo();
        schedulerJobInfo.setCronJob(false);
        schedulerJobInfo.setJobClass(JobExecutor.class.getName());
        schedulerJobInfo.setRepeatTime(2000L);
        schedulerJobInfo.setStartDate(LocalDateTime.now());

        schedulerJobInfo.setJobData(jobData);

        schedulerJobService.saveOrUpdate(schedulerJobInfo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "Pause a scheduler job.")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "Scheduler job paused"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/pause/{jobName}/{jobGroup}")
    public ResponseEntity<String> pauseSchendulerJob(
            @PathVariable String jobName,
            @PathVariable String jobGroup){

        JobKey jobKey = new JobKey(jobName, jobGroup);

        boolean paused = schedulerJobService.pauseJob(jobKey);

        if (paused) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }else{

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no job scheduled with the given name and group.");
        }
    }


    @ApiOperation(value = "Resume a scheduler job.")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "Scheduler job resumed"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @GetMapping("/resume/{jobName}/{jobGroup}")
    public ResponseEntity<String> resumeSchendulerJob(@PathVariable String jobName, @PathVariable String jobGroup){
        JobKey jobKey = new JobKey(jobName, jobGroup);

        boolean paused = schedulerJobService.resumeJob(jobKey);

        if (paused) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no job scheduled with the given name and group.");
        }
    }

    @ApiOperation(value = "Delete a scheduler job.")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "Scheduler job deleted"),
            @ApiResponse(code = 500, message = "An exception was thrown on the server"),
    })

    @DeleteMapping("/delete/{jobName}/{jobGroup}")
    public ResponseEntity<String> deleteSchendulerJob(@PathVariable String jobName, @PathVariable String jobGroup){
        JobKey jobKey = new JobKey(jobName, jobGroup);

        boolean paused = schedulerJobService.deleteJob(jobKey);

        if (paused) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no job scheduled with the given name and group.");
        }
    }






}
