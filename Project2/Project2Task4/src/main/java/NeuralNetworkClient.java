import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

/**
 * NeuralNetworkClient
 *
 * This UDP client communicates with a Neural Network server to:
 *   - Display the current truth table.
 *   - Set custom truth table values.
 *   - Perform training (single or multiple steps).
 *   - Test the network with custom inputs.
 *   - Exit the program.
 *
 * Requests and responses are exchanged in JSON format using the Gson library.
 *
 * JSON Request formats include:
 *   {"request": "getCurrentRange"}
 *   {"request": "setCurrentRange", "val1": double, "val2": double, "val3": double, "val4": double}
 *   {"request": "train", "iterations": int}
 *   {"request": "test", "val1": double, "val2": double}
 *
 * JSON Response examples include:
 *   {"response": "getCurrentRange", "status": "OK", "val1": double, "val2": double, "val3": double, "val4": double}
 *   {"response": "setCurrentRange", "status": "OK"}
 *   {"response": "train", "status": "OK", "val1": totalError}
 *   {"response": "test", "status": "OK", "val1": double}
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class NeuralNetworkClient {
    private static InetAddress serverAddress;
    private static int serverPort = 6789; // Default server port
    private static DatagramSocket clientSocket;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        System.out.println("Neural Network Client is running.");
        try {
            // Initialize connection with the server.
            serverAddress = InetAddress.getByName("localhost");
            clientSocket = new DatagramSocket();

            Scanner scanner = new Scanner(System.in);
            int choice;

            // Main client loop.
            while (true) {
                // Display menu options.
                System.out.println("\nMain Menu:");
                System.out.println("0. Display current truth table.");
                System.out.println("1. Set truth table values.");
                System.out.println("2. Perform a single training step.");
                System.out.println("3. Perform n training steps.");
                System.out.println("4. Test with a pair of inputs.");
                System.out.println("5. Exit program.");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                // Option 5: Exit the client.
                if (choice == 5) {
                    System.out.println("Client side quitting.");
                    break;
                }

                JsonObject request = new JsonObject();
                switch (choice) {
                    case 0:
                        // Request the current truth table from the server.
                        request.addProperty("request", "getCurrentRange");
                        break;
                    case 1:
                        // Set truth table values manually.
                        System.out.println("Enter four values (each 0 or 1) for the truth table:");
                        request.addProperty("request", "setCurrentRange");
                        request.addProperty("val1", scanner.nextDouble());
                        request.addProperty("val2", scanner.nextDouble());
                        request.addProperty("val3", scanner.nextDouble());
                        request.addProperty("val4", scanner.nextDouble());
                        break;
                    case 2:
                        // Perform a single training step.
                        request.addProperty("request", "train");
                        request.addProperty("iterations", 1);
                        break;
                    case 3:
                        // Perform multiple training steps.
                        System.out.print("Enter number of training iterations: ");
                        int steps = scanner.nextInt();
                        request.addProperty("request", "train");
                        request.addProperty("iterations", steps);
                        break;
                    case 4:
                        // Test the network with a pair of input values.
                        System.out.println("Enter two input values for testing:");
                        request.addProperty("request", "test");
                        request.addProperty("val1", scanner.nextDouble());
                        request.addProperty("val2", scanner.nextDouble());
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                }
                // Send the JSON request and process the JSON response.
                String responseJson = sendRequest(request.toString());
                processResponse(responseJson, choice);
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            if (clientSocket != null)
                clientSocket.close();
        }
    }

    /**
     * Sends a JSON-formatted request to the server and waits for a JSON response.
     *
     * @param jsonRequest The JSON request string.
     * @return The JSON response string.
     * @throws IOException If an I/O error occurs.
     */
    private static String sendRequest(String jsonRequest) throws IOException {
        byte[] data = jsonRequest.getBytes();
        DatagramPacket requestPacket = new DatagramPacket(data, data.length, serverAddress, serverPort);
        clientSocket.send(requestPacket);

        byte[] buffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
        clientSocket.receive(responsePacket);
        return new String(responsePacket.getData(), 0, responsePacket.getLength());
    }

    /**
     * Processes the JSON response received from the server.
     *
     * @param jsonResponse The JSON response string.
     * @param choice The menu choice that triggered this request.
     */
    private static void processResponse(String jsonResponse, int choice) {
        JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
        String status = response.get("status").getAsString();
        if (!status.equals("OK")) {
            System.out.println("Error processing request.");
            return;
        }
        switch (choice) {
            case 0: // Display current truth table.
                System.out.println("Current truth table:");
                System.out.println("Row 1: 0.0 0.0 " + response.get("val1").getAsDouble());
                System.out.println("Row 2: 0.0 1.0 " + response.get("val2").getAsDouble());
                System.out.println("Row 3: 1.0 0.0 " + response.get("val3").getAsDouble());
                System.out.println("Row 4: 1.0 1.0 " + response.get("val4").getAsDouble());
                break;
            case 2: // Single training step.
                System.out.println("Error after training step: " + response.get("val1").getAsDouble());
                break;
            case 3: // Multiple training steps.
                System.out.println("Training complete. Total error: " + response.get("val1").getAsDouble());
                break;
            case 4: // Test result.
                System.out.println("Test output: " + response.get("val1").getAsDouble());
                break;
            default:
                break;
        }
    }
}
