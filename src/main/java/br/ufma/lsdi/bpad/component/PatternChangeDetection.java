package br.ufma.lsdi.bpad.component;

import br.ufma.lsdi.bpad.model.*;
import br.ufma.lsdi.bpad.util.FileUtil;
import br.ufma.lsdi.bpad.util.GlobalDateSingleton;
import br.ufma.lsdi.bpad.util.PatternDetectionUtil;
import com.google.gson.Gson;
import net.sourceforge.jFuzzyLogic.FIS;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PatternChangeDetection {

    @Autowired
    private GlobalDateSingleton globalDateSingleton;

    @Autowired
    private Gson gson;

    public BehavioralPatternChange detectPattenChange(BehavioralPattern currentPattern, BehavioralPattern patternDetected, JobData jobData,
                                                      JobKey jobKey, String ctx) {

        LocalDate currentDate = globalDateSingleton.getCurrentGlobalDate(jobKey);

        AlgorithmParam algorithmParam = jobData.getAlgorithmParam();

        double similarity = PatternDetectionUtil.calcSimilarity(currentPattern.getDailyRoutine(),
                patternDetected.getDailyRoutine(), 1440); //todo similaridade de menor granularidade


        FIS fis = FileUtil.readFIS();

        if (fis == null) {
            System.out.println("Can't load FCL");
            return null;
        } else {
            fis.setVariable("sensibility", algorithmParam.getSensitivity());
            fis.setVariable("similarity", similarity * 100);
            fis.evaluate();

            double defuzzyFiedValue = fis.getFunctionBlock("drift").getVariable("drift").getValue();
            double changeValue = fis.getFunctionBlock("drift").getVariable("drift").getMembership("change");
            double noChangeValue = fis.getFunctionBlock("drift").getVariable("drift").getMembership("no_change");

            if (changeValue > noChangeValue){
                return new BehavioralPatternChange(null, jobKey.toString(), jobData.getUid(), currentDate, jobData.getBehaviors(),
                        ctx, similarity, defuzzyFiedValue,  changeValue, patternDetected.getDailyRoutine(), currentPattern.getDailyRoutine());
            }else{
                return null;
            }
        }
    }

}
