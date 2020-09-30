package com.example.producehelper.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
    public static Date getToday()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date addDate(Date date,int year,int month,int day,int hour,int minute,int second,int millisecond)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);//加减年数
        c.add(Calendar.MONTH, month);//加减月数
        c.add(Calendar.DATE, day);//加减天数
        c.add(Calendar.HOUR,hour);//加减小时数
        c.add(Calendar.MINUTE, minute);//加减分钟数
        c.add(Calendar.SECOND, second);//加减秒
        c.add(Calendar.MILLISECOND, millisecond);//加减毫秒数

        return c.getTime();
    }

    public static String parseDateToStr(Date time, String timeFromat)
    {
        DateFormat dateFormat=new SimpleDateFormat(timeFromat);
        return dateFormat.format(time);
    }

    public static Date parseStrToDate(String time, String timeFromat)
    {
        if (time == null || time.equals(""))
        {
            return null;
        }

        Date date=null;
        try
        {
            DateFormat dateFormat=new SimpleDateFormat(timeFromat);
            date=dateFormat.parse(time);
        }
        catch(Exception e)
        {

        }
        return date;
    }
}
