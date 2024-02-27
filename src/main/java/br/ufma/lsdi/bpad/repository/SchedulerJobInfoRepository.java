package br.ufma.lsdi.bpad.repository;

import br.ufma.lsdi.bpad.model.SchedulerJobInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchedulerJobInfoRepository extends MongoRepository<SchedulerJobInfo, String> {

    SchedulerJobInfo findSchedulerJobInfoByJobData_JobNameAndJobData_JobGroup(String jobName, String jobGroup);

}
