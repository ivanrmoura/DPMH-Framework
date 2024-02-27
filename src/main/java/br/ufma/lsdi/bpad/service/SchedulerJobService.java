package br.ufma.lsdi.bpad.service;


import br.ufma.lsdi.bpad.component.JobScheduleCreator;
import br.ufma.lsdi.bpad.model.JobData;
import br.ufma.lsdi.bpad.model.SchedulerJobInfo;
import br.ufma.lsdi.bpad.repository.SchedulerJobInfoRepository;
import br.ufma.lsdi.bpad.util.GlobalDateSingleton;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class SchedulerJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SchedulerJobInfoRepository schedulerRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    @Autowired
    private GlobalDateSingleton globalDate;


    public SchedulerMetaData getMetaData() throws SchedulerException {
        SchedulerMetaData metaData = scheduler.getMetaData();
        return metaData;
    }

    public List<SchedulerJobInfo> getAllJobList() {
        return schedulerRepository.findAll();
    }

    public void startAllSchedulers() {
        List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        jobInfoList.forEach(jobInfo -> {
            JobData jobData = jobInfo.getJobData();
            try {
                JobDetail jobDetail = JobBuilder.newJob(
                                (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                        .withIdentity(jobData.getJobName(), jobData.getJobGroup()).build();
                if (!scheduler.checkExists(jobDetail.getKey())) {
                    Trigger trigger;
                    jobDetail = scheduleCreator.createJob(
                            (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                            false, context, jobData.getJobName(), jobData.getJobGroup(), jobInfo.getJobData());

                    if (jobInfo.getCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
                        trigger = scheduleCreator.createCronTrigger(jobData.getJobName(), new Date(),
                                jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                    } else {
                        trigger = scheduleCreator.createSimpleTrigger(jobData.getJobName(), new Date(),
                                jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
                    }

                    globalDate.newGlobalDate(jobDetail.getKey());

                    scheduler.scheduleJob(jobDetail, trigger);
                    jobInfo.setJobStatus("SCHEDULED");

                    schedulerRepository.save(jobInfo);

                    System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " scheduled.");
                }
                System.out.println(">>>>> Started all schedulers.");
            } catch (ClassNotFoundException e) {
                System.out.println("Class Not Found - "+jobInfo.getJobClass());
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        });
    }


    public SchedulerJobInfo saveOrUpdate(SchedulerJobInfo scheduleJob) {

        SchedulerJobInfo jobPersisted;

        if (scheduleJob.getId() == null) {
            jobPersisted = scheduleNewJob(scheduleJob);
        } else {
            jobPersisted = updateScheduleJob(scheduleJob);

        }

        return jobPersisted;
    }


    private SchedulerJobInfo scheduleNewJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobData jobData = jobInfo.getJobData();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobData.getJobName(), jobData.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {

                jobDetail = scheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                        false, context,
                        jobData.getJobName(),
                        jobData.getJobGroup(),
                        jobInfo.getJobData());

                Trigger trigger;
                if (jobInfo.getCronJob()) {
                    trigger = scheduleCreator.createCronTrigger(
                            jobData.getJobName(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = scheduleCreator.createSimpleTrigger(
                            jobData.getJobName(),
                            new Date(),
                            jobInfo.getRepeatTime(),

                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }


                globalDate.newGlobalDate(jobInfo.getJobKey());
                jobInfo.setJobStatus("SCHEDULED");
                SchedulerJobInfo jobInfoSalved = schedulerRepository.save(jobInfo);

                scheduler.scheduleJob(jobDetail, trigger);

                System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " scheduled.");
                return jobInfoSalved;

            } else {
                System.out.println("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private SchedulerJobInfo updateScheduleJob(SchedulerJobInfo jobInfo) {
        Trigger newTrigger;
        JobData jobData = jobInfo.getJobData();
        if (jobInfo.getCronJob()) {

            newTrigger = scheduleCreator.createCronTrigger(
                    jobData.getJobName(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {

            newTrigger = scheduleCreator.createSimpleTrigger(
                    jobData.getJobName(),
                    new Date(),
                    jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            jobInfo.setJobStatus("EDITED & SCHEDULED");
            SchedulerJobInfo jobInfoSalved = schedulerRepository.save(jobInfo);
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobData.getJobName()), newTrigger);

            System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " updated and scheduled.");
            return jobInfoSalved;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public boolean startJobNow(JobKey jobKey) {
        try {
            SchedulerJobInfo jobInfo = schedulerRepository.findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(jobKey.getName(), jobKey.getGroup());
            JobData jobData = jobInfo.getJobData();
            jobInfo.setJobStatus("SCHEDULED & STARTED");
            schedulerRepository.save(jobInfo);
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobData.getJobName(), jobData.getJobGroup()));
            System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " scheduled and started now.");
            return true;
        } catch (SchedulerException e) {
            System.out.println("Failed to start new job - "+jobKey);
            return false;
        }
    }

    public boolean pauseJob(JobKey jobKey) {
        try {
            SchedulerJobInfo getJobInfo = schedulerRepository.findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(jobKey.getName(), jobKey.getGroup());
            getJobInfo.setJobStatus("PAUSED");
            schedulerRepository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().deleteJob(jobKey);
            System.out.println(">>>>> jobName = [" + jobKey.getName() + "]" + " paused.");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to pause job - "+jobKey.getName());
            return false;
        }
    }

    public boolean resumeJob(JobKey jobKey) {
        try {

            SchedulerJobInfo jobInfo = schedulerRepository.findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(jobKey.getName(), jobKey.getGroup());
            JobData jobData = jobInfo.getJobData();
            jobInfo.setJobStatus("RESUMED");
            schedulerRepository.save(jobInfo);
            scheduleNewJob(jobInfo);
            System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " resumed.");
            return true;
        }catch (Exception e){
            System.out.println("Failed to resume job - "+jobKey.getName());
            return false;
        }
    }

    public boolean deleteJob(JobKey jobKey) {
        try {
            SchedulerJobInfo jobInfo = schedulerRepository.findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(jobKey.getName(), jobKey.getGroup());
            JobData jobData = jobInfo.getJobData();
            schedulerRepository.delete(jobInfo);
            System.out.println(">>>>> jobName = [" + jobData.getJobName() + "]" + " deleted.");
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobData.getJobName(), jobData.getJobGroup()));
        } catch (SchedulerException e) {
            System.out.println("Failed to delete job - "+jobKey);
            return false;
        }
    }

}
