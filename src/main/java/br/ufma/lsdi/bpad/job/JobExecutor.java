package br.ufma.lsdi.bpad.job;

import br.ufma.lsdi.bpad.component.AbnormalBehaviorDetection;
import br.ufma.lsdi.bpad.component.EventBusAdmin;
import br.ufma.lsdi.bpad.component.ObservationManager;
import br.ufma.lsdi.bpad.component.PatternChangeDetection;
import br.ufma.lsdi.bpad.model.*;
import br.ufma.lsdi.bpad.repository.AbnormalBehaviorRepository;
import br.ufma.lsdi.bpad.repository.BehaviorPatternChangeRepository;
import br.ufma.lsdi.bpad.repository.BehavioralPatternRepository;
import br.ufma.lsdi.bpad.repository.SchedulerJobInfoRepository;
import br.ufma.lsdi.bpad.service.PatternDetectionService;
import br.ufma.lsdi.bpad.service.SchedulerJobService;
import br.ufma.lsdi.bpad.util.GlobalDateSingleton;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;



@Slf4j
@DisallowConcurrentExecution
public class JobExecutor extends QuartzJobBean {

    @Autowired
    private Gson gson;

    @Autowired
    private GlobalDateSingleton globalDate;

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private ObservationManager observationManager;

    @Autowired
    private BehavioralPatternRepository patternRepository;

    @Autowired
    private PatternDetectionService patternDetection;

    @Autowired
    private AbnormalBehaviorDetection abnormalBehaviorDetection;

    @Autowired
    private PatternChangeDetection patternChangeDetection;

    @Autowired
    private AbnormalBehaviorRepository abnormalBehaviorRepository;

    @Autowired
    private BehaviorPatternChangeRepository behaviorPatternChangeRepository;

    @Autowired
    EventBusAdmin eventBusAdmin;

    private JobData jobData;

    private LocalDate currentDate;

    private JobKey jobKey;

    private SchedulerJobInfo jobInfo;

    @Autowired
    private SchedulerJobInfoRepository schedulerJobInfoRepository;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) {

        jobKey = context.getJobDetail().getKey();
        jobInfo = schedulerJobInfoRepository.findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(jobKey.getName(), jobKey.getGroup());
        jobData = jobInfo.getJobData();

        AlgorithmParam algorithmParam = jobData.getAlgorithmParam();
        currentDate = globalDate.getCurrentGlobalDate(jobKey);
        LocalDate endDate = LocalDate.of(2013,6,1);
        System.out.println(""+jobKey+": "+currentDate);

        if (!currentDate.equals(endDate)) {

            String contextsOfObs = observationManager.selectContextsOfObservation(jobData.getUid(), currentDate);
            if (contextsOfObs != null) {
                if (contextsOfObs.split(",").length > 2){
                    System.out.println("Contexto inesperado");
                }

                //Verifica qual os contextos das observações
                for (String ctx : contextsOfObs.split(",")) {
                    if (jobData.getContextAttributes().contains(ctx)) {

                        ObservationRecord observationRecord = observationManager.updateObservationRecorde(
                                jobData.getUid(), jobKey.toString(), ctx, currentDate);

                        BehavioralPattern currentPattern = patternRepository.findCurrentBehavioralPattern(jobKey.toString(), jobData.getUid(), ctx);

                        int numObs = observationRecord.getObservationDates().size();

                        if (currentPattern == null){
                            detectFirstPattern(ctx, observationRecord.getObservationDates(), numObs);
                        }else{
                            List<LocalDate>  date = Arrays.asList(globalDate.getCurrentGlobalDate(jobKey));

                            BehavioralPattern patternDetected = patternDetection.detectPattern(jobInfo, ctx, date, currentDate);

                            detectAbnormalBehavior(currentPattern, patternDetected, ctx);

                            detectBehaviorPatternChange(currentPattern, observationRecord, patternDetected, numObs, ctx);
                        }

                        if (numObs == algorithmParam.getNumObs()){
                            observationManager.cleanObservationRecord(observationRecord);
                        }
                    }
                }
            }

            //atualiza o relógio global
            globalDate.plusGlobalDate(jobKey, 1);
        }else{
            schedulerJobService.pauseJob(jobKey);
            System.out.println("----------------- Pausando Job: "+jobKey);
        }

    }

    private void detectBehaviorPatternChange(BehavioralPattern currentPattern, ObservationRecord observationRecord,
                                             BehavioralPattern patternDetected, int numObs, String ctx) {

        if (numObs == jobData.getAlgorithmParam().getNumObs()){

            BehavioralPatternChange patternChange = patternChangeDetection.detectPattenChange(currentPattern, patternDetected, jobData, jobKey, ctx);

            if (patternChange != null) {
                System.out.println("Mudança de padrão: " + gson.toJson(patternChange));
                behaviorPatternChangeRepository.save(patternChange);

                currentPattern.setCurrentPattern(false);
                patternRepository.save(currentPattern);

                //salva o novo padrão
                patternDetected.setCurrentPattern(true);
                patternRepository.save(patternDetected);
                System.out.println("Novo padrão: " + gson.toJson(patternDetected));
            }

            observationManager.cleanObservationRecord(observationRecord);
        }
    }

    private void detectAbnormalBehavior(BehavioralPattern currentPattern, BehavioralPattern patternDetected, String ctx) {

        AbnormalBehavior abnormalBehavior = abnormalBehaviorDetection.detectAbnormalBehavior(currentPattern, patternDetected, jobData, jobKey, ctx);
        if (abnormalBehavior != null) {
            System.out.println("comportamento anormal: "+gson.toJson(abnormalBehavior));
            abnormalBehaviorRepository.save(abnormalBehavior);

            // posta no barramento
            //eventBusAdmin.postEvent(abnormalBehavior);
        }
    }

    private void detectFirstPattern(String ctx, List<LocalDate> observationDates, int numObs) {

        if (numObs == jobData.getAlgorithmParam().getNumObs()) {
            BehavioralPattern behavioralPattern = patternDetection.detectPattern(jobInfo, ctx, observationDates, currentDate);
            behavioralPattern.setCurrentPattern(true);
            patternRepository.save(behavioralPattern);
        }

    }

    private JobData getJobData(JobExecutionContext context){
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String jobDataJSON = dataMap.getString("jobData");
        return gson.fromJson(jobDataJSON, JobData.class);
    }
}
