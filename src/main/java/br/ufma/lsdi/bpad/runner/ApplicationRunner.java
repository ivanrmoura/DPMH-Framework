package br.ufma.lsdi.bpad.runner;


import br.ufma.lsdi.bpad.component.ConnectionManager;
import br.ufma.lsdi.bpad.job.JobExecutor;
import br.ufma.lsdi.bpad.model.AlgorithmParam;
import br.ufma.lsdi.bpad.model.BehavioralPattern;
import br.ufma.lsdi.bpad.model.JobData;
import br.ufma.lsdi.bpad.model.SchedulerJobInfo;
import br.ufma.lsdi.bpad.repository.*;
import br.ufma.lsdi.bpad.service.SchedulerJobService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ApplicationRunner implements CommandLineRunner {

    @Autowired
    BehavioralPatternRepository behavioralPatternRepository;

    @Autowired
    private ObservationRecordRepository observationRecordRepository;

    @Autowired
    private AbnormalBehaviorRepository abnormalBehaviorRepository;

    @Autowired
    private BehaviorPatternChangeRepository patternChangeRepository;

    @Autowired
    private SchedulerJobInfoRepository schedulerJobInfoRepository;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private Gson gson;

    @Override
    public void run(String... args) throws InterruptedException, IOException {

        connectionManager.startConnect();

        //schedulerJobService.startAllSchedulers();

        //cleanCollectionsMongo();

        //SchedulerJobInfo jobInfo = createSchendulerJob(null);

        //schedulerJobService.saveOrUpdate(jobInfo);

    }

    private void cleanCollectionsMongo(){
        observationRecordRepository.deleteAll(); //todo remover quando for para produção
        behavioralPatternRepository.deleteAll();
        abnormalBehaviorRepository.deleteAll();
        patternChangeRepository.deleteAll();
        schedulerJobInfoRepository.deleteAll();
    }

    private SchedulerJobInfo createSchendulerJob(String schendulerJobId){
        SchedulerJobInfo jobInfo = new SchedulerJobInfo();

        jobInfo.setId(schendulerJobId);
        //jobInfo.setJobName("lucas.sociability");
        //jobInfo.setJobGroup("lsdi");
        jobInfo.setCronJob(false);
        jobInfo.setJobClass(JobExecutor.class.getName());
        jobInfo.setRepeatTime(2000L);
        jobInfo.setStartDate(LocalDateTime.now());

        jobInfo.setJobData(getJobData());

        System.out.println(gson.toJson(getJobData()));


        return jobInfo;
    }


    private JobData getJobData(){
        JobData jobData = new JobData();
        jobData.setJobName("lucas.sociability");
        jobData.setJobGroup("LSDi");
        jobData.setUid("u01");
        jobData.setBehaviors(Arrays.asList("convesartion", "sociability"));
        jobData.setContextAttributes(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"));
        AlgorithmParam param = new AlgorithmParam( 1, 0.05, 2, 50);
        jobData.setAlgorithmParam(param);

        return jobData;
    }

}
