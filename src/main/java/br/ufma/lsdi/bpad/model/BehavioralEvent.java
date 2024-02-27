package br.ufma.lsdi.bpad.model;

import br.ufma.lsdi.bpad.util.SlotUtil;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.*;
import java.util.Arrays;
import java.util.List;

@Data
@Document
public class BehavioralEvent {

    @Id
    private String id;

    private String uid;

    private LocalDateTime timestamp;

    private Instant persistenceTimestamp;

    private String behavior;

    private String[] contexts;

    private String[] availableAttributes;

    private Object[] serviceValue;

    private Double accuracy;

    private GeoJsonPoint location;

    public List<Integer> getSlots(int numSlots){

        Timestamp startDateTime = Timestamp.valueOf((LocalDateTime) serviceValue[0]);
        Timestamp endDateTime = Timestamp.valueOf((LocalDateTime) serviceValue[1]);;

        Integer[] slots = SlotUtil.extractSlotsFull(startDateTime, endDateTime, numSlots);
        return Arrays.asList(slots);
    }

    public Integer getSlot(int numSlots, boolean isEndTime, ZoneId zoneId){

        //Timestamp startDateTime = Timestamp.valueOf(LocalDateTime.ofInstant(timestamp, zoneId));
        Timestamp startDateTime = Timestamp.valueOf(timestamp);
        return SlotUtil.extractSlot(startDateTime, isEndTime, numSlots);
    }
}
