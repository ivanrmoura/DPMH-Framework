package br.ufma.lsdi.bpad.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String toLocalDate(LocalDate localDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    public static LocalDateTime toLocalDateTime(String dateSTR){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "yyyy-MM-dd HH:mm:ss" );
        return LocalDateTime.parse (dateSTR, formatter );
    }

    public static String getWeek(int day){
        if (day >= 6){
            return "WEEKEND";
        }else{
            return "WEEK";
        }
    }
}
