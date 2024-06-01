package com.example.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component

@RestController
public class WeatherController {

    /**
    * @return 東京の天気情報
    * 
    * @see <a href="http://weather.livedoor.com/weather_hacks/webservice">お天気Webサービス - livedoor</a>
    */
    @RequestMapping(value = "weather/tokyo", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String call() {

        RestTemplate rest = new RestTemplate();

        final String cityCode = "130010"; // 東京のCityCode
        final String endpoint = "http://weather.livedoor.com/forecast/webservice/json/v1";

        final String url = endpoint + "?city=" + cityCode;

        ResponseEntity<String> response = rest.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return "{\"error\": \"Failed to retrieve weather data\"}";
        }

        String json = response.getBody();
        return decode(json);
    }

    // JSON文字列から日本語をデコードする
    private static String decode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(json).toString();
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to decode JSON\"}";
        }
    }
    
    @Autowired
    NyappiCall nyappi;

    @Scheduled(cron = "${schedule.nyappi_call.cron}")
    public void cron() {
        this.nyappi.randomcall();
    }
}
