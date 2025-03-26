import java.net.*;
import java.io.*;
import java.util.*;

/**
 * RemoteVariableServerUDP
 *
 * This UDP server maintains a separate sum for each client, identified by a unique ID.
 * It listens on a specified port and accepts requests in the format "ID,operation[,value]".
 * Supported operations:
 *   - "add": Adds the provided value to the client's sum.
 *   - "subtract": Subtracts the provided value from the client's sum.
 *   - "get": Returns the current sum for the client.
 *
 * The server stores client sums in a TreeMap and runs indefinitely until it receives the "halt!" command.
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class RemoteVariableServerUDP {
    // Map to store the sum for each client (userID -> sum)
    private static final Map<Integer, Integer> userSums = new TreeMap<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        byte[] buffer = new byte[1000]; // Buffer to hold incoming data

        try {
            // Announce server startup and prompt for listening port.
            System.out.println("Server started");
            System.out.println("Enter the port number to listen on:");
            BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(portReader.readLine());

            // Create a DatagramSocket bound to the specified port.
            socket = new DatagramSocket(serverPort);

            // Process incoming requests indefinitely.
            while (true) {
                // Receive a UDP packet from a client.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                // Convert the received data into a String.
                String receivedData = new String(request.getData(), 0, request.getLength()).trim();

                // Check for the "halt!" command to shut down the server.
                if (receivedData.equals("halt!")) {
                    System.out.println("Server shutting down.");
                    break;
                }

                // Split the request into components: userID, operation, and optional value.
                String[] parts = receivedData.split(",");
                int userID = Integer.parseInt(parts[0]);      // Unique client ID
                String operation = parts[1];                   // Operation: add, subtract, or get
                int value = parts.length > 2 ? Integer.parseInt(parts[2]) : 0; // Value (if provided)

                // Initialize the client's sum if this is the first request for the ID.
                userSums.putIfAbsent(userID, 0);

                // Process the operation.
                if (operation.equals("add")) {
                    userSums.put(userID, userSums.get(userID) + value);
                } else if (operation.equals("subtract")) {
                    userSums.put(userID, userSums.get(userID) - value);
                }
                // For "get", no change is made; simply return the current sum.

                // Retrieve the updated sum for the given userID.
                int sum = userSums.get(userID);
                // Log the details of the request and the current sum.
                System.out.println("ID: " + userID + " | Operation: " + operation + " | Value: " + value + " | Sum: " + sum);

                // Prepare the response packet with the current sum.
                byte[] replyData = String.valueOf(sum).getBytes();
                DatagramPacket reply = new DatagramPacket(replyData, replyData.length, request.getAddress(), request.getPort());
                // Send the response back to the client.
                socket.send(reply);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Close the socket to free resources.
            if (socket != null) socket.close();
        }
    }
}
