import java.net.*;
import java.io.*;

/**
 * RemoteVariableClientUDP
 *
 * This UDP client allows a user to interact with a remote variable server.
 * The client presents a menu with four options:
 *   1. Add a value to your sum.
 *   2. Subtract a value from your sum.
 *   3. Get your sum.
 *   4. Exit the client.
 *
 * For each operation (except "get"), the client prompts for a value and a user ID.
 * The client then constructs a request string in the format: "ID,operation[,value]"
 * and sends it to the server. The server's response (the updated sum) is then displayed.
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class RemoteVariableClientUDP {
    public static void main(String[] args) {
        // Announce client startup.
        System.out.println("The client is running.");
        System.out.println("Please enter server port:");

        try (BufferedReader portReader = new BufferedReader(new InputStreamReader(System.in))) {
            // Read the server port from user input.
            int serverPort = Integer.parseInt(portReader.readLine());
            // Resolve the server's address (localhost).
            InetAddress serverAddress = InetAddress.getByName("localhost");
            // Create a UDP socket for communication.
            DatagramSocket socket = new DatagramSocket();

            // BufferedReader to capture user commands.
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // Display the client menu.
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client.");

                // Read the user's choice.
                String choice = userInput.readLine();
                if (choice.equals("4")) {
                    System.out.println("Client side quitting.");
                    break;
                }

                // Prompt for the user ID.
                System.out.println("Enter your ID:");
                String userID = userInput.readLine();
                String operation = "";
                String value = "";

                // Set operation and prompt for value if necessary.
                if (choice.equals("1")) {
                    operation = "add";
                    System.out.println("Enter value to add:");
                    value = userInput.readLine();
                } else if (choice.equals("2")) {
                    operation = "subtract";
                    System.out.println("Enter value to subtract:");
                    value = userInput.readLine();
                } else if (choice.equals("3")) {
                    operation = "get";
                }

                // Formulate the request string: "ID,operation" or "ID,operation,value".
                String requestData = userID + "," + operation + (value.isEmpty() ? "" : "," + value);
                // Send the request and retrieve the server's response.
                int result = sendRequest(requestData, socket, serverAddress, serverPort);
                // Display the result to the user.
                System.out.println("The result is " + result + ".");
            }
            // Close the socket once the client exits.
            socket.close();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    /**
     * Sends the request to the server and returns the result.
     *
     * @param input         The request string in the format "ID,operation[,value]".
     * @param socket        The DatagramSocket used for communication.
     * @param serverAddress The InetAddress of the server.
     * @param serverPort    The port number on which the server is listening.
     * @return              The integer result returned by the server.
     * @throws IOException  If an I/O error occurs during communication.
     */
    public static int sendRequest(String input, DatagramSocket socket, InetAddress serverAddress, int serverPort) throws IOException {
        // Convert the request string into bytes.
        byte[] sendData = input.getBytes();
        // Create a UDP packet to send to the server.
        DatagramPacket request = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        // Send the request packet.
        socket.send(request);

        // Prepare a buffer to receive the server's response.
        byte[] buffer = new byte[1000];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        // Wait for the response.
        socket.receive(response);
        // Convert the response to a string, trim whitespace, and parse it as an integer.
        return Integer.parseInt(new String(response.getData(), 0, response.getLength()).trim());
    }
}
