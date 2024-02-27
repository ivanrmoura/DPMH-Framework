package br.ufma.lsdi.bpad.util;

import org.quartz.JobKey;

import java.time.LocalDate;
import java.util.HashMap;

public class GlobalDateSingleton {

    private final HashMap<JobKey, LocalDate> cloks = new HashMap<>();

    public void newGlobalDate(JobKey key){
        cloks.put(key, LocalDate.of(2013, 3, 31));
    }

    public void plusGlobalDate(JobKey key, int days){
        cloks.put(key, cloks.get(key).plusDays(days));
    }

    public LocalDate getCurrentGlobalDate(JobKey key){
        return cloks.get(key);
    }

    public LocalDate getOldGlobalDate(JobKey key, int days){
        return cloks.get(key).plusDays(-days);
    }





}
