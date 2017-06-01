package com.tyq_code.tanbomonitor.tools;

import java.io.Serializable;
import java.util.Calendar;

public class DateTranslator implements Serializable {
    private int year;
    private int month;
    private int day;
    private String string;

    DateTranslator(int y, int m, int d) {
        year = y;
        month = m;
        day = d;
        string = year + "-" + (month > 9 ? month : ("0" + month)) + "-" + (day > 9 ? day : ("0" + day));
    }

    DateTranslator(String d) {
        string = d;
        year = (string.charAt(0) - '0') * 1000 + (string.charAt(1) - '0') * 100 + (string.charAt(2) - '0') * 10 + (string.charAt(3) - '0');
        month = (string.charAt(5) - '0') * 10 + (string.charAt(6) - '0');
        day = (string.charAt(8) - '0') * 10 + (string.charAt(9) - '0');
    }

    DateTranslator(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        string = year + "-" + (month > 9 ? month : ("0" + month)) + "-" + (day > 9 ? day : ("0" + day));
    }

    public String getString() {
        return string;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
