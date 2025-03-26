package ds.project1task3.controller;

import ds.project1task3.model.NationalParkModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Vishakha Pathak
 * Servlet to handle user requests for national park information.
 *
 * This servlet receives park selection data from the front-end, fetches park details
 * (such as images, weather conditions, and activities) using the NationalParkModel,
 * and forwards the results to a JSP for display.
 *
 * LLM Self-Reporting: Used an LLM to improve error handling by identifying areas where
 * validation was missing and refining exception handling.
 *
 * **Prompt used for LLM self-reporting:**
 * ```
 * Improve error handling in this Java servlet. Ensure proper validation for request parameters,
 * handle missing or invalid values gracefully, and forward errors to an error page.
 * Also, suggest improvements in exception handling.
 * ```
 */
@WebServlet("/nationalpark")
public class NationalParkServlet extends HttpServlet {
    private final NationalParkModel model = new NationalParkModel();

    /**
     * Handles POST requests to retrieve park data.
     *
     * @param request  HTTP request containing park selection parameters.
     * @param response HTTP response to forward data or errors.
     * @throws ServletException In case of forwarding failures.
     * @throws IOException      In case of input/output errors.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract request parameters
        String parkCode = request.getParameter("park");
        String parkName = request.getParameter("parkName");
        String latStr = request.getParameter("lat");
        String lonStr = request.getParameter("lon");
        String apiKey = "taJF4eZHn5WqzpzT0KhNXYmAeUNzvXOTuh2auZhz"; // API Key (consider externalizing this)

        // Validate input parameters to prevent NullPointerException
        if (parkCode == null || parkName == null || latStr == null || lonStr == null) {
            handleError(request, response, "Missing required parameters.");
            return;
        }

        double lat, lon;
        try {
            // Convert latitude and longitude to double
            lat = Double.parseDouble(latStr);
            lon = Double.parseDouble(lonStr);
        } catch (NumberFormatException e) {
            handleError(request, response, "Invalid latitude or longitude format.");
            return;
        }

        try {
            // get park image and credit details
            String[] image = model.getParkImage(parkCode);
            if (image == null || image.length < 2) {
                handleError(request, response, "Failed to retrieve park image.");
                return;
            }
            String parkImage = image[0];
            String imageCredit = image[1];

            // get weather information
            String weather = model.getWeather(lat, lon);
            if (weather == null) {
                handleError(request, response, "Failed to retrieve weather data.");
                return;
            }

            // Fetch park activities
            String activities = model.getActivities(parkCode, apiKey);
            if (activities == null) {
                handleError(request, response, "Failed to retrieve activities data.");
                return;
            }

            // Set request attributes and forward data to JSP for rendering
            request.setAttribute("parkName", parkName);
            request.setAttribute("parkImage", parkImage);
            request.setAttribute("imageCredit", imageCredit);
            request.setAttribute("weather", weather);
            request.setAttribute("activities", activities);
            request.getRequestDispatcher("result.jsp").forward(request, response);

        } catch (IOException e) {
            // LLM Self-Reporting: The error handling here was enhanced based on LLM suggestions.
            handleError(request, response, "Error fetching data: " + e.getMessage());
        }
    }

    /**
     * Handles errors by forwarding them to an error page.
     *
     * @param request      HTTP request object.
     * @param response     HTTP response object.
     * @param errorMessage The error message to be displayed.
     * @throws ServletException In case of forwarding failures.
     * @throws IOException      In case of input/output errors.
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }
}
