package br.ufma.lsdi.bpad.service;

import br.ufma.lsdi.bpad.model.AlgorithmParam;
import br.ufma.lsdi.bpad.model.BehavioralPattern;
import br.ufma.lsdi.bpad.model.SchedulerJobInfo;
import br.ufma.lsdi.bpad.model.TimeInterval;
import br.ufma.lsdi.bpad.util.PatternDetectionUtil;
import br.ufma.lsdi.bpad.util.UDFS;
import com.google.gson.Gson;
import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static org.apache.spark.sql.functions.*;

@Component
public class PatternDetectionService {

    @Autowired
    private SparkSession spark;

    @Autowired
    private Gson gson;


    public BehavioralPattern detectPattern(SchedulerJobInfo jobInfo, String context, List<LocalDate> dateList, LocalDate currentDate) {

        String uid = jobInfo.getJobData().getUid();
        List<String> behaviorList = jobInfo.getJobData().getBehaviors();
        AlgorithmParam algorithmParam = jobInfo.getJobData().getAlgorithmParam();

        //Registra as UDFS
        registerUDFS();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        Dataset<Row> phenotypeEventsDs = MongoSpark.load(jsc).toDF();

        String[] behaviors = behaviorList.toArray(new String[0]);
        String[] dates = dateList.stream().map(String::valueOf).toArray(String[]::new);

        double PHI = algorithmParam.getPhi();
        double THETA = algorithmParam.getTheta();

        Dataset<Row> phenotypeEventsFilted = phenotypeEventsDs.select(col("uid"),
                        col("contexts"),
                        col("serviceValue").getItem(0).as("startDateTime"),
                        col("serviceValue").getItem(1).as("endDateTime"),
                        date_format(col("timestamp"), "yyyy-MM-dd").as("date"))
                .filter(array_contains(col("contexts"), context)
                        .and(col("uid").equalTo(uid)))
                .withColumn("behaviors", lit(behaviors))
                .withColumn("dates", lit(dates))
                .withColumn("context", lit(context))
                .withColumn("duration", expr("(unix_timestamp(endDateTime) - unix_timestamp(startDateTime))/60"))
                .filter(array_contains(col("behaviors"), col("behavior"))
                        .and(array_contains(col("dates"), col("date"))));

        //phenotypeEventsFilted.show();

        // calcular o tamanho do slot com base na média da duração dos eventos
        int NUMSLOTS = calcSlotSize(phenotypeEventsFilted);

        // Filtra os eventos e atribui os slots aos eventos
        WindowSpec windowSpec = Window.orderBy("id");
        Dataset<Row> eventCountBySlot = phenotypeEventsFilted
                .withColumn("slots", functions.callUDF("slotsUDF", col("startDateTime"),
                        col("endDateTime"), lit(NUMSLOTS)))
                .select(col("uid"),
                        col("behaviors"),
                        col("context"),
                        explode(col("slots")).alias("slot"))
                .groupBy("uid", "context", "slot")
                .agg(count("slot").alias("count"))
                .orderBy("uid", "context", "slot")
                .withColumn("id", lit(1))
                .withColumn("numEvents", sum("count").over(windowSpec));

        //eventCountBySlot.show();

        // Seleciona os slots candidatos
        Dataset<Row> candidateSlotsDf = eventCountBySlot
                .withColumn("interval_th", round(col("numEvents").multiply(PHI), 2))
                .withColumn("slot_candidate_th", round(col("numEvents").multiply(THETA * ((double) 1 / NUMSLOTS)), 2))
                .filter(col("count").geq(col("slot_candidate_th")));


        List<Row> candidates = candidateSlotsDf.collectAsList();
        if (candidates.size() == 0) {
            System.out.println("nenhum slot candidato detectado");
        }

        // identifica os intervalos frequentes (rotina)
        List<TimeInterval> dailyRoutine =  PatternDetectionUtil.patternExtraction(candidates, NUMSLOTS);

       return new BehavioralPattern(
                null, false,
                jobInfo.getJobKey().toString(),
                jobInfo.getJobData().getUid(),
                currentDate,
                jobInfo.getJobData().getBehaviors(),
                context,
                dailyRoutine,
                NUMSLOTS);
    }

    // usar a mediana
    private int calcSlotSize(Dataset<Row> phenotypeEventsFilted){

        Dataset<Row> meanDurationDF = phenotypeEventsFilted.groupBy("context", "uid")
                .agg(count("context").alias("count"),
                        mean("duration").alias("meanDuration"));
        meanDurationDF.show();
        List<Row> meanDurationList = meanDurationDF.collectAsList();

        int meanDuration = (int) Double.parseDouble(meanDurationList.get(0).getAs("meanDuration").toString());

        if (meanDuration < 1){
            return 1;
        }

        while (1440 % meanDuration != 0){
            meanDuration = meanDuration - 1;
        }

        return 1440 / meanDuration;
    }

    private  void registerUDFS() {
        spark
                .sqlContext()
                .udf()
                .register( "slotsUDF", UDFS.slotsUDF(), DataTypes.createArrayType(DataTypes.IntegerType) );
    }
}
