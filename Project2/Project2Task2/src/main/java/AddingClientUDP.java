// AddingClientUDP.java
import java.net.*;
import java.io.*;

/**
 * AddingClientUDP
 *
 * This UDP client connects to a server running on localhost at a port specified by the user.
 * It reads arithmetic expressions (or other numeric input) from the console, sends them to the server,
 * and prints the integer result returned by the server. If the user types "halt!", the client terminates.
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class AddingClientUDP {
    public static void main(String[] args) {
        // Announce client startup.
        System.out.println("The client is running.");
        System.out.println("Please enter server port:");

        try (
                // Create a BufferedReader to capture the server port number from the user.
                BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Read the port number where the server is listening.
            int serverPort = Integer.parseInt(portReader.readLine());
            // Resolve the server's address (localhost).
            InetAddress serverAddress = InetAddress.getByName("localhost");
            // Create a DatagramSocket for sending and receiving UDP packets.
            DatagramSocket socket = new DatagramSocket();

            // Create a second BufferedReader to read arithmetic expressions from the user.
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String input;
            // Continue reading input until the user terminates (or types "halt!").
            while ((input = userInput.readLine()) != null) {
                // If the user types "halt!", print a quitting message and exit the loop.
                if (input.trim().equals("halt!")) {
                    System.out.println("Client side quitting.");
                    break;
                }
                // Call the add() method to send the expression to the server and receive a result.
                int result = add(input, socket, serverAddress, serverPort);
                // Display the result returned by the server.
                System.out.println("The server returned " + result);
            }
            // Close the socket once done.
            socket.close();
        } catch (IOException e) {
            // Display an error message if any I/O exception occurs.
            System.out.println("Client error: " + e.getMessage());
        }
    }

    /**
     * Sends the user's input to the server and waits for the response.
     *
     * @param input         The arithmetic expression or number as a String.
     * @param socket        The DatagramSocket used for communication.
     * @param serverAddress The InetAddress of the server.
     * @param serverPort    The port number on which the server is listening.
     * @return              The integer result returned by the server.
     * @throws IOException  If an I/O error occurs during communication.
     */
    public static int add(String input, DatagramSocket socket, InetAddress serverAddress, int serverPort) throws IOException {
        // Convert the input string to a byte array.
        byte[] sendData = input.getBytes();
        // Create a UDP packet with the input data destined for the server.
        DatagramPacket request = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        // Send the packet via the socket.
        socket.send(request);

        // Prepare a buffer and a packet to receive the server's reply.
        byte[] buffer = new byte[1000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        // Wait for the server's response.
        socket.receive(response);
        // Convert the response bytes to a String, trim whitespace, and parse as an integer.
        return Integer.parseInt(new String(response.getData(), 0, response.getLength()).trim());
    }
}
