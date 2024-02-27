package br.ufma.lsdi.bpad.util;

import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.api.java.UDF3;

import java.sql.Timestamp;

public class UDFS {
    public static UDF2<Timestamp, Integer, Integer> slotUDF()
    {
        return ( s1, s2 ) -> {
            if ( s1 != null )
            {
                return SlotUtil.extractSlot(s1, false, s2 );
            }
            else
            {
                return null;
            }
        };
    }

    public static UDF3<Timestamp, Timestamp, Integer, Integer[]> slotsUDF()
    {
        return ( s1, s2, s3 ) -> {
            if ( s1 != null )
            {
                return SlotUtil.extractSlotsFull(s1,s2,s3);
            }
            else
            {
                return null;
            }
        };
    }

}
