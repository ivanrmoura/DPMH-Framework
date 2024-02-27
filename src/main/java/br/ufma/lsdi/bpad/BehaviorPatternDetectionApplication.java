package br.ufma.lsdi.bpad;

import br.ufma.lsdi.bpad.component.BehaviorEventPersistence;
import br.ufma.lsdi.bpad.component.ConnectionManager;
import br.ufma.lsdi.bpad.component.EventBusAdmin;
import br.ufma.lsdi.bpad.util.GlobalDateSingleton;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@OpenAPIDefinition
public class BehaviorPatternDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BehaviorPatternDetectionApplication.class, args);
    }

    @Bean
    public SparkSession sparkSessionFactoryBean(){
        return SparkSession
                .builder()
                .appName("Detect Pattern DPMH")
                .master("local[*]")
                .config("spark.executor.memory", "1g")
                .config("spark.driver.memory", "1g")
                .config("spark.memory.offHeap.enabled", true)
                .config("spark.memory.offHeap.size", "16g")
                .config("spark.mongodb.input.uri", "mongodb://localhost:27017/bpad-mongodb")//digitalPhenotypeEvent")
                .config("spark.mongodb.input.collection", "behavioralEvent")
                //.config("spark.mongodb.output.uri", "mongodb://localhost:27017/bpad-mongodb")
                .getOrCreate();

    }

    @Bean
    @Scope("singleton")
    public GlobalDateSingleton globalDateSingletonFactoryBean() {
        return new GlobalDateSingleton();
    }



    @Bean
    public Gson gsonFactoryBean(){
        Converters.registerOffsetTime(new GsonBuilder()).create();
        return buildGson();
        //return new GsonBuilder().setPrettyPrinting().create();
    }

    private Gson buildGson(){
        return  new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, jsonDeserializationContext)
                                -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, type, jsonDeserializationContext)
                                -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>)
                        (json, type, jsonDeserializationContext)
                                -> LocalTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("HH:mm")))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
                    return new JsonPrimitive(src.toString()); // "yyyy-mm-dd";
                })
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> {
                    return new JsonPrimitive(src.toString()); // "yyyy-mm-dd";
                })
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> {
                    return new JsonPrimitive(src.toString()); // "yyyy-mm-dd";
                })
                .create();
    }


    @Bean
    @Scope("singleton")
    public EventBusAdmin eventBusAdminFactoryBean() {
        return new EventBusAdmin();
    }

    @Bean
    @Scope("singleton")
    public ConnectionManager connectionManagerFactoryBean(){
        return new ConnectionManager();
    }

    @Bean
    @Scope("singleton")
    public BehaviorEventPersistence behaviorEventPersistenceFactoryBean(){
        return new BehaviorEventPersistence();
    }



}
