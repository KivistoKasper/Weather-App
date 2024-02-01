
package com.mycompany.forecasthub.Controllers;

import com.mycompany.forecasthub.Models.TimeData;
import com.mycompany.forecasthub.Services.TimeDataService;
import java.io.IOException;

/**
 * Controller for time data.
 * This controller acts as a middle hand with the time API and saving the data
 * to time model.
 * Use this controller for getting time data.
 * @author kasper
 */
public class TimeDataController {
    
    private TimeData Data;
    private TimeDataService TimeService;
    private String TimeZone;

    // constructor
    public TimeDataController(String timeZone) {
        // save the timeZone
        this.TimeZone = timeZone; 
        
        // fetch the API data
        try {
            this.TimeService = new TimeDataService(timeZone);
        } 
        catch (IOException ex) {
            System.out.println(ex);
        }
        
        // place it in the model
        this.Data = new TimeData(TimeService.getData());        
    }
    
    public void updateAll() {
        // fetch new data
        try {
            this.TimeService = new TimeDataService(TimeZone);
        } 
        catch (IOException ex) {
            System.out.println(ex);
        }
        
        // place it in the model
        this.Data = new TimeData(TimeService.getData());
    }
    
    public String getDate() {
        return Data.getDate();
    }
    // return time in hh:mm:ss
    public String getTime() {
        return Data.getTime();
    }
    // return day of the week in int; example: sunday=0, monday=1, tuesday=2 etc...
    public int getWeekday() {
        return Data.getWeekday();
    }
    // return number of the week
    public int getWeekNumber() {
        return Data.getWeekNumber();
    }
    // return the time zone
    public String getTimeZone() {
        return Data.getTimeZone();
    }
    // return the utc offset
    public String getUtcOffset() {
        return Data.getUtcOffset();
    }
    // return the utc offset in double
    public double getUtcOffsetInt() {
        String offset = Data.getUtcOffset().substring(1, Data.getUtcOffset().length()-4);
        return Double.parseDouble(offset);
    }
    
    // get everything in a string. used mostly for debugging.
    @Override
    public String toString() {
        return Data.toString();
    }
    
}
