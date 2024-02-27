package br.ufma.lsdi.bpad.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

@Data
public class JobData {

    @NotBlank(message = "jobName is mandatory")
    private String jobName;

    @NotBlank(message = "jobGroup is mandatory")
    private String jobGroup;

    @NotBlank(message = "uid is mandatory")
    private String uid;

    @NotBlank(message = "algorithmParam is mandatory")
    private AlgorithmParam algorithmParam;

    @NotBlank(message = "behaviors is mandatory")
    private List<String> behaviors;

    @NotBlank(message = "contextAttributes is mandatory")
    private List<String> contextAttributes;




}
