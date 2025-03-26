import java.net.*;
import java.io.*;

/**
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 * This class represents a UDP client that sends messages to a server and receives echoed responses.
 * The client connects to a server running on localhost and a user-specified port.
 * It terminates when the user enters the "halt!" command.
 */
public class EchoClientUDP {
    public static void main(String args[]) {
        DatagramSocket aSocket = null; // Socket for sending and receiving UDP packets
        try {
            // Announce client startup
            System.out.println("The UDP client is running.");
            // Prompt user for the server port number
            System.out.println("Enter the server port number:");
            BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = Integer.parseInt(portReader.readLine()); // Read port number from user
            InetAddress aHost = InetAddress.getByName("localhost"); // Resolve server address
            aSocket = new DatagramSocket(); // Create a socket for communication

            // Main client loop to send and receive messages
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                byte[] m = nextLine.getBytes(); // Convert the input string to a byte array
                // Prepare and send the request to the server
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                aSocket.send(request);

                // Prepare to receive the server's reply
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);

                // Trim the buffer to the actual length of the received data
                byte[] trimmedBuffer = new byte[reply.getLength()];
                System.arraycopy(reply.getData(), 0, trimmedBuffer, 0, reply.getLength());
                // Display the server's reply
                System.out.println("Reply from server: " + new String(trimmedBuffer));

                // Check for the "halt!" command to terminate the client
                if (nextLine.trim().equals("halt!")) {
                    System.out.println("UDP Client side quitting"); // Announce client shutdown
                    break; // Exit the loop
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage()); // Handle socket errors
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage()); // Handle I/O errors
        } finally {
            if (aSocket != null) aSocket.close(); // Ensure the socket is closed
        }
    }
}