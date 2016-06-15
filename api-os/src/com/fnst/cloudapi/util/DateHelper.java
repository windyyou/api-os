package com.fnst.cloudapi.util;

import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelper {
	
	/**
	 * get a Date Object by input String 
	 * @param datestr
	 * @return null if the String is invalid
	 */
	public static Date getDateByString(String datestr){
		SimpleDateFormat sdf= null;
		Date date=null;		
		if (datestr.indexOf("Z")<0){
			sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		}else if(datestr.indexOf(".")>-1){		
			sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		}else{
			sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");			
		}

		try {
//			System.out.println(sdf.toPattern());
			date= sdf.parse(datestr);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return date;
	}
	
    /**
     * date object to int
     * @param date  Calendar
     * @return int value
     */
    public static int DateToInt(Calendar date){
        return (int) (date.getTimeInMillis() / 1000);
    }

    public static long StrToIntDateHour(String time)
    {

        String strtime=time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+" "+time.substring(9,11)+":00:00";
        return StrToIntDateFull(strtime);
    }

    /**
     *    datestring to longvalue
     * @param date string yyyy-MM-dd HH:mm:ss
     * @return long value
     */
    public static long StrToIntDateFull(String date){
       long intdate= Timestamp.valueOf(date).getTime() / 1000;
       return intdate;
    }

    /**
     * dateint vlue to datestring day
     * @param dateInt
     * @return datestring yyyy/MM/dd
     */
    public static String intToDateStrDay(int dateInt) {
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd ");
        String timestring=df.format(time.getTime());
        return timestring;
    }
    /**
     * dateint vlue to datestring day
     * @param dateInt
     * @return datestring yyyy-MM-dd
     */
    public static String intToDateStrDay2(int dateInt) {
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        String timestring=df.format(time.getTime());
        return timestring;
    }

    /**
     * dateint to datestring secend
     * @param dateInt
     * @return  datestring yyyy-MM-dd HH:mm:ss
     */
     public static String intToDateTimeFull(int dateInt) {
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestring=df.format(time.getTime());
        return timestring;
    }
    /**
     * dateint to datestring secend
     * @param dateInt
     * @return  datestring yyyy-MM-dd HH:mm:ss
     */
     public static String intToDateTimeFull2(int dateInt) {
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestring=df.format(time.getTime());
        return timestring;
    }

    /**
     * inttime to stringtime HH:mm:ss
     * @param dateInt
     * @return shorttime string HH:mm:ss
     */
    public static String intToDateTimeShort(int dateInt) {
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String timestring=df.format(time.getTime());
        return timestring;
    }

    /**
     * intdate to Date Object
     * @param dateInt secend
     * @return dateObject
     */
    public static Date intToDateObject(int dateInt){
        Calendar time = Calendar.getInstance();
        Long l = new Long(Integer.toString(dateInt));
        time.setTimeInMillis(l.longValue() * 1000);
        return time.getTime();
    }
	
	public static void main(String arg[]){
		
		String str="2016-04-26T07:58:51.495313Z";
		
		String str2="2016-04-26T08:48:01.030174";
		String str3="2016-04-26T09:48:00Z";
		
		System.out.println(DateHelper.getDateByString(str).toLocaleString());
		System.out.println(DateHelper.getDateByString(str2).toLocaleString());
		
		System.out.println(DateHelper.getDateByString(str3).toLocaleString());
		
		
		
	}

}
