// AddingServerUDP.java
import java.net.*;
import java.io.*;

/**
 * AddingServerUDP
 *
 * This UDP server listens on a user-specified port, receives numeric input from a client,
 * adds the input to a running sum, and returns the updated sum. If the server receives
 * the message "halt!", it shuts down.
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class AddingServerUDP {
    // Shared variable to maintain the running sum.
    private static int sum = 0;

    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000]; // Buffer to hold incoming data

        try {
            // Announce server startup and prompt for listening port.
            System.out.println("Server started.");
            System.out.println("Enter the port number to listen on:");

            // Read the port number from the console.
            BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(portReader.readLine());

            // Create a socket bound to the specified port.
            aSocket = new DatagramSocket(serverPort);

            // Continuously receive and process client messages.
            while (true) {
                // Create a DatagramPacket to receive data.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                // Convert received data to a string using the correct length.
                String receivedData = new String(request.getData(), 0, request.getLength()).trim();

                // If the message is "halt!", exit the loop and shut down.
                if (receivedData.equals("halt!")) {
                    System.out.println("Server shutting down.");
                    break;
                }

                // Parse the received string as a number.
                int receivedNumber = parseNumber(receivedData);
                if (receivedNumber != Integer.MIN_VALUE) {
                    System.out.println("Adding " + receivedNumber + " to " + sum);
                    sum += receivedNumber;
                    System.out.println("Returning new sum " + sum + " to client");
                }

                // Convert the updated sum to bytes and send it back to the client.
                byte[] replyData = String.valueOf(sum).getBytes();
                DatagramPacket reply = new DatagramPacket(replyData, replyData.length,
                        request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Ensure the socket is closed.
            if (aSocket != null)
                aSocket.close();
        }
    }

    /**
     * Attempts to parse the provided string as an integer.
     * If parsing fails, it prints an error and returns Integer.MIN_VALUE.
     *
     * @param data The string to be parsed.
     * @return The integer value or Integer.MIN_VALUE if parsing fails.
     */
    private static int parseNumber(String data) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number received: " + data);
            return Integer.MIN_VALUE;
        }
    }
}
