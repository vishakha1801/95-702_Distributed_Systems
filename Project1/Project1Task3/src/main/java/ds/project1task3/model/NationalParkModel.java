package ds.project1task3.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Vishakha Pathak
 * Model class to retrieve National Park-related data such as images, weather, and activities.
 *
 * This class interacts with:
 * - National Park Service (NPS) API to get activities.
 * - Weather.gov for real-time weather data.
 * - Web scraping using jsoup to get park images from NPS websites.
 *
 * LLM Self-Reporting: Used an LLM to improve error handling and optimize API response parsing.
 * Improve error handling and make sure API responses are handled safely.
 */
public class NationalParkModel {
    private static final String WEATHER_API_BASE = "https://forecast.weather.gov/MapClick.php";
    private static final String NPS_API_BASE = "https://developer.nps.gov/api/v1/parks";

    /**
     * Retrieves an image of the national park along with a credit source.
     *
     * @param parkCode The unique code of the national park.
     * @return A String array where index 0 is the image URL and index 1 is the credit source.
     */
    public String[] getParkImage(String parkCode) {
        String url = String.format("https://www.nps.gov/%s/index.htm", parkCode);
        try {
            Document doc = Jsoup.connect(url).get();
            String imageUrl = doc.select("meta[property=og:image]").attr("content");

            if (!imageUrl.isEmpty()) {
                return new String[]{imageUrl, "www.nps.gov"};
            }
        } catch (IOException e) {
            System.err.println("Error retrieving image for park code " + parkCode + ": " + e.getMessage());
        }

        return new String[]{"https://via.placeholder.com/800x400?text=Image+Unavailable", "Image Unavailable"};
    }

    /**
     * Retrieves the current weather conditions for a given latitude and longitude.
     *
     * @param latitude  The latitude of the national park.
     * @param longitude The longitude of the national park.
     * @return A formatted string containing temperature, humidity, and wind speed.
     * @throws IOException If there's an issue retrieving data from the weather website.
     */
    public String getWeather(double latitude, double longitude) throws IOException {
        String url = WEATHER_API_BASE + "?lat=" + latitude + "&lon=" + longitude;
        Document doc = Jsoup.connect(url).get();

        return String.format("Current conditions:<br>Temperature: %s<br>Humidity: %s<br>Wind speed: %s",
                doc.select(".myforecast-current-lrg").text(),
                doc.select(".panel-body").select("td:contains(Humidity) + td").text(),
                doc.select(".panel-body").select("td:contains(Wind Speed) + td").text());
    }

    /**
     * Retrieves a list of activities available at the national park.
     *
     * @param parkCode The unique code of the national park.
     * @param apiKey   API key for accessing the National Park Service API.
     * @return A formatted string containing the list of activities.
     * @throws IOException If there's an issue retrieving data from the API.
     */
    public String getActivities(String parkCode, String apiKey) throws IOException {
        String url = String.format("%s?parkCode=%s&api_key=%s", NPS_API_BASE, parkCode, apiKey);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray dataArray = jsonObject.getAsJsonArray("data");

        if (dataArray == null || dataArray.isEmpty()) {
            return "No activities available.";
        }

        JsonArray activities = dataArray.get(0).getAsJsonObject().getAsJsonArray("activities");

        if (activities == null || activities.isEmpty()) {
            return "No activities found for this park.";
        }

        StringBuilder activitiesList = new StringBuilder();
        for (int i = 0; i < activities.size(); i++) {
            activitiesList.append(activities.get(i).getAsJsonObject().get("name").getAsString()).append("<br>");
        }

        return activitiesList.toString();
    }
}
