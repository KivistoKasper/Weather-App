
package com.mycompany.forecasthub.Models;
import com.google.gson.JsonObject;

/**
 * TimeData is the model for saving the time data from TimeDataService.
 * It also gives the asked data to a controller.
 * @author Kasper PC
 */
public class TimeData {
    
    private final JsonObject RawData;
    // data for specific city/timezone
    private String Date;
    private String Time;
    private int Weekday;
    private int WeekNumber;
    private String TimeZone;
    private String UtcOffset;
    
    // functions for placing the data in place
    
    // set date in yyyy/mm/dd format 
    private void setDate() {
        String dateTime = RawData.get("datetime").toString();
        this.Date = dateTime.substring(1, 11);
    }
    // set time in hh:mm:ss
    private void setTime() {
        String dateTime = RawData.get("datetime").toString();
        this.Time = dateTime.substring(12, 20);
    }
    // set day of the week in int; example: sunday=0, monday=1, tuesday=2 etc...
    private void setWeekday() {
        this.Weekday = RawData.get("day_of_week").getAsInt();
    }
    // set number of the week
    private void setWeekNumber() {
        this.WeekNumber = RawData.get("week_number").getAsInt();
    }
    // set the time zone
    private void setTimeZone() {
        this.TimeZone = RawData.get("timezone").toString();
    }
    // set the utc offset
    private void setUtcOffset() {
        this.UtcOffset = RawData.get("utc_offset").toString();
    }

    // constructor for TimeData model. Called from controller with a JsonObject
    // provided by API.
    public TimeData(JsonObject rawData) {
        this.RawData = rawData;
        setDate();
        setTime();
        setWeekday();
        setWeekNumber();
        setTimeZone();
        setUtcOffset();
        
    }
    
    // getters
    // returns date in yyyy/mm/dd format 
    public String getDate() {
        return Date;
    }
    // return time in hh:mm:ss
    public String getTime() {
        return Time;
    }
    // return day of the week in int; example: sunday=0, monday=1, tuesday=2 etc...
    public int getWeekday() {
        return Weekday;
    }
    // return number of the week
    public int getWeekNumber() {
        return WeekNumber;
    }
    // return the time zone
    public String getTimeZone() {
        return TimeZone;
    }
    // return the utc offset
    public String getUtcOffset() {
        return UtcOffset;
    }
    

    @Override
    public String toString() {
        return "date and time: " + Date + " : " + Time + "\n" +
        "weekday: " + Weekday + "\n" +
        "number of the week: " + WeekNumber + "\n" +
        "timezone: " + TimeZone + "\n" +
        "Utc offset: " + UtcOffset;
    }
    
    
    
    
    
}
