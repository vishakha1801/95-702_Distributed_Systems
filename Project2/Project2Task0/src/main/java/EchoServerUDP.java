import java.net.*;
import java.io.*;

/**
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 * This class represents a UDP server that echoes back messages received from a client.
 * The server listens on a user-specified port and handles one client at a time.
 * It terminates when it receives the "halt!" command from the client.
 */
public class EchoServerUDP {
    public static void main(String args[]) {
        DatagramSocket aSocket = null; // Socket for receiving and sending UDP packets
        byte[] buffer = new byte[1000]; // Buffer to store incoming data
        try {
            // Announce server startup
            System.out.println("The UDP server is running.");
            // Prompt user for the port number to listen on
            System.out.println("Enter the port number to listen on:");
            BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(portReader.readLine()); // Read port number from user
            aSocket = new DatagramSocket(serverPort); // Create socket bound to the specified port

            // Main server loop to handle client requests
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length); // Prepare to receive data
                aSocket.receive(request); // Wait for a client request

                // Trim the buffer to the actual length of the received data
                byte[] trimmedBuffer = new byte[request.getLength()];
                System.arraycopy(request.getData(), 0, trimmedBuffer, 0, request.getLength());
                String requestString = new String(trimmedBuffer); // Convert byte array to string

                // Display the received message
                System.out.println("Echoing: " + requestString);

                // Prepare and send the reply back to the client
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);

                // Check for the "halt!" command to terminate the server
                if (requestString.trim().equals("halt!")) {
                    System.out.println("UDP Server side quitting"); // Announce server shutdown
                    break; // Exit the loop
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle socket errors
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage()); // Handle I/O errors
        } finally {
            if (aSocket != null) aSocket.close(); // Ensure the socket is closed
        }
    }
}