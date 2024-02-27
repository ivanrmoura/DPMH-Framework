package br.ufma.lsdi.bpad.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class SlotUtil {

    // extrai o número de slots referente aos minutos
    public static int getSlotMinute(double w, int time, boolean isEndTime) {

        int min = (int)(60 / (1 / w));

        if (time == 0){
            if (isEndTime) {
                return 0;
            }else{
                return 1;
            }
        } else if (time == min){
            if (isEndTime) {
                return time/min;
            }else{
                return time/min + 1;
            }
        }else if (time<min) {
            return 1;
        } else {
            return time/min + 1;
        }
    }

    /*public static int extractSlot(LocalDateTime datetime){
        double w = configParameters.getT();
        int hourEvent = datetime.getHour();
        int minuteEvent = datetime.getMinute();
        int slot = (int)(hourEvent/w) + getSlotMinute(w, minuteEvent);
        return slot;
    }*/

    public static Integer extractSlot(Timestamp datetime, boolean isEndTime, int numSlots){

        double slotSize = 1440 / numSlots;

        double hours = datetime.getHours();
        double minutes = datetime.getMinutes();

        double sumMinutes = (hours * 60) + minutes;

        if (hours == 0 && minutes == 0 && isEndTime){
            return numSlots;
        } else if (hours == 0 && minutes == 0){
            return 1;
        } else{
            return (int) (sumMinutes / slotSize) + 1;
        }
    }

    public static Integer[] extractSlotsFull(Timestamp startTime, Timestamp endTime, int numSlots){
        ArrayList<Integer> listSlots = new ArrayList<>();

        int startSlot = SlotUtil.extractSlot(startTime, false, numSlots);
        int endSlot = SlotUtil.extractSlot(endTime,  true, numSlots);

        listSlots.add(startSlot);

        if (startSlot == endSlot){
            Integer[] slots = new Integer[listSlots.size()];
            return listSlots.toArray(slots);
        }else{
            while (startSlot < endSlot){
                startSlot += 1;
                listSlots.add(startSlot);
            }
            Integer[] slots = new Integer[listSlots.size()];
            return listSlots.toArray(slots);
        }
    }


    public static LocalTime slotToDate(int slot, Boolean startSlot, int numSlots)  {

        double slotSize = 1440 / numSlots;
        double slotTime = (slot * slotSize) / 60;

        int hour = (int) slotTime;
        int min = (int) ((slotTime - hour) * 60);

        if (hour == 24){
            hour = 0;
        }
        LocalTime time = LocalTime.of(hour, min);
        if (startSlot){
            time = time.plusMinutes((long) -slotSize);
        }
        return time;
    }



    /*public static LocalTime slotToDate(int slot, Boolean startSlot, double w)  {

        double unit_temp = w * 60;
        Double slot_w = slot * w;
        int slot_w_int = slot_w.intValue();
        double slot_w_frac = slot_w - slot_w_int;

        int hora;
        int minute;
        if (slot_w_frac == 0){
            if (startSlot){
                hora = slot_w_int - 1;
                minute = (int) (60 - unit_temp);
            }else{
                hora = slot_w_int;
                minute = 0;
            }
        }else{
            hora = slot_w_int;
            if (startSlot){
                minute = (int) ((slot_w_frac - w) * 60);
            }else{
                minute = (int) ((slot_w_frac) * 60);
            }
        }
        if (hora == 24){
            hora = 0;
        }
        return LocalTime.of(hora, minute);
    }
*/
    public static long toTimestamp(LocalDateTime datetime){
        ZonedDateTime zdt = datetime.atZone(ZoneId.of("America/Sao_Paulo"));
        return zdt.toInstant().toEpochMilli();
    }

}
