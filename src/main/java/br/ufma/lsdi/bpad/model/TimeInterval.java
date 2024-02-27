package br.ufma.lsdi.bpad.model;

import br.ufma.lsdi.bpad.util.SlotUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;



@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeInterval {

    private String startTime;

    private String endTime;

    public List<Integer> getSlots(int numSlots){

        try {
            LocalDate localDate = LocalDate.now();
            Timestamp startDateTime = Timestamp.valueOf(localDate.atTime(LocalTime.parse(this.startTime)));
            Timestamp endDateTime = Timestamp.valueOf(localDate.atTime(LocalTime.parse(this.endTime)));

            Integer[] slots = SlotUtil.extractSlotsFull(startDateTime, endDateTime, numSlots);
            return Arrays.asList(slots);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

}
