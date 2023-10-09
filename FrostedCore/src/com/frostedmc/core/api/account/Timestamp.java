package com.frostedmc.core.api.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Redraskal_2 on 8/25/2016.
 */
public class Timestamp {

    public Date t;
    public SimpleDateFormat f;

    public Timestamp(Date time) {
        t = time;
        f = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
    }

    public String toString() {
        return f.format(t);
    }

    public Date toDate() {
        try {
            return f.parse(toString());
        } catch (ParseException e) {e.printStackTrace(); return new Date(); }
    }

    @SuppressWarnings("deprecation")
    public boolean canEnd() {
        return t.getYear() == 2001;
    }

    public static Timestamp getNeverEndingStamp() {
        return new Timestamp(parse("10/03/2001 1:00:00 PM"));
    }

    public static Date parse(String time) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(time);
        } catch (ParseException e) {e.printStackTrace(); return new Date(); }
    }

    public static Timestamp getCurrentTimestamp() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
        return new Timestamp(Calendar.getInstance().getTime());
    }
}