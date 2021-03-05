package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class CmsDateUtils {

    public static LocalDateTime parseDate(String date, String time) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        if (StringUtils.isEmpty(time)) {
            time = "00:00:00";
        } else {
            // il manque le premier 0
            if (time.length() == 4) {
                time = "0" + time;
            }
            // il manque les secondes
            if (time.length() == 5) {
                time += ":00";
            }
        }
        String dateString = date + " " + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CmsUtils.DATETIME_FORMAT);
        LocalDateTime dateBegin = null;
        try {
            dateBegin = LocalDateTime.parse(dateString, formatter);
        }catch(DateTimeParseException ignore){}

        return dateBegin;
    }

    public static Date LocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date LocalDateTimeToDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getYearStringFromDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(date);
    }

    /*
    public static Date getBeginDateYear(int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }


    public static Date getEndDateYear(int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
     */

    public static LocalDateTime getBeginDateYear(int year){
        return LocalDateTime.of(year, 1,1, 0, 0, 0);
    }
    public static LocalDateTime getEndDateYear(int year){
        return LocalDateTime.of(year, 12,31, 23, 59, 59);
    }

    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
