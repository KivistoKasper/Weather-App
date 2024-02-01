
package com.mycompany.forecasthub.Services;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * TimeDataService fetches time data from the API.
 * It also declares the getters.
 * @author Kasper Kivistö
 */

/* format of the JSON from API
 *{
  "abbreviation": "EEST",
  "client_ip": "84.250.51.107",
  "datetime": "2023-10-09T21:26:17.626122+03:00",
  "day_of_week": 1,
  "day_of_year": 282,
  "dst": true,
  "dst_from": "2023-03-26T01:00:00+00:00",
  "dst_offset": 3600,
  "dst_until": "2023-10-29T01:00:00+00:00",
  "raw_offset": 7200,
  "timezone": "Europe/Helsinki",
  "unixtime": 1696875977,
  "utc_datetime": "2023-10-09T18:26:17.626122+00:00",
  "utc_offset": "+03:00",
  "week_number": 41
} 
 * 
 */

public class TimeDataService {

    
    private JsonObject timeData;
    
    // timeData constructor
    // timezone is in the format of: "Europe/Helsinki"
    // refer to this cite for others: http://worldtimeapi.org/timezones
    public TimeDataService(String timezone) throws MalformedURLException, IOException {
        
        String urlTimezone = timezone;
        String urlBody = "http://worldtimeapi.org/api/timezone/";
        
        try {
            // the url to the API
            URL url = new URL(urlBody + urlTimezone);
            int timeout = 3000; // timeout after 3s
            
            // establish connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            int responsecode = connection.getResponseCode(); // 200 if okay
            
            // check if response if good
            if ( responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            }
            else { // all good, continue
                
                JsonElement rawData;
                try (Scanner scanner = new Scanner(url.openStream())) {
                    Gson gson = new Gson();
                    String json = scanner.nextLine();
                    rawData = gson.fromJson(json, JsonElement.class);
                }
                // final data as JSON
                this.timeData = rawData.getAsJsonObject();
//                System.out.println("timeData: " + timeData);
            }
            
        } catch (Error e) {
            System.out.print(e);
        }
    }

    // return the data
    public JsonObject getData() {
        return timeData;
    }
    
    
}
    
