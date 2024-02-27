package br.ufma.lsdi.bpad.model;

import lombok.Data;
import org.quartz.JobKey;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document
@Data
public class SchedulerJobInfo {

    @Id
    private String id;

    private String jobStatus;

    private String jobClass;

    private String cronExpression;

    private Long repeatTime;

    private Boolean cronJob;

    private LocalDateTime startDate;

    @NotBlank(message = "JobData is mandatory")
    private JobData jobData;


    public JobKey getJobKey(){
        return new JobKey(this.getJobData().getJobName(), this.getJobData().getJobGroup());
    }


}
