import java.net.*;
import java.io.*;

/**
 * EavesdropperUDP
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 * Intercepts UDP messages on a specified (deceptive) port, modifies the first
 * occurrence of the standalone word "like" to "dislike", forwards the message
 * to the real server, and relays the server's response back to the client.
 * Runs indefinitely.
 */
public class EavesdropperUDP {
    public static void main(String[] args) {
        DatagramSocket eavesdropSocket = null;  // For receiving and relaying messages
        DatagramSocket forwardSocket = null;      // For forwarding messages to the real server

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("UDP Eavesdropper running.");
            System.out.print("Enter deceptive listening port: ");
            int listenPort = Integer.parseInt(in.readLine());
            System.out.print("Enter real server's port: ");
            int serverPort = Integer.parseInt(in.readLine());

            eavesdropSocket = new DatagramSocket(listenPort);
            forwardSocket = new DatagramSocket();
            byte[] buffer = new byte[1000];
            System.out.println("Listening on port " + listenPort + " and forwarding to port " + serverPort);

            while (true) {
                // Receive message from client
                DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
                eavesdropSocket.receive(clientPacket);
                String clientMsg = new String(clientPacket.getData(), 0, clientPacket.getLength());
                System.out.println("Intercepted: " + clientMsg);

                // Modify message if it contains "like"
                String modifiedMsg = clientMsg.replaceFirst("\\blike\\b", "dislike");
                if (!clientMsg.equals(modifiedMsg)) {
                    System.out.println("Modified to: " + modifiedMsg);
                }

                // Forward (modified) message to real server
                byte[] forwardData = modifiedMsg.getBytes();
                DatagramPacket forwardPacket = new DatagramPacket(
                        forwardData, forwardData.length, InetAddress.getByName("localhost"), serverPort);
                forwardSocket.send(forwardPacket);
                System.out.println("Sent to server: " + modifiedMsg);

                // Receive server's response and relay it to client
                DatagramPacket serverReply = new DatagramPacket(buffer, buffer.length);
                forwardSocket.receive(serverReply);
                String serverMsg = new String(serverReply.getData(), 0, serverReply.getLength());
                System.out.println("Server replied: " + serverMsg);

                DatagramPacket replyPacket = new DatagramPacket(
                        serverReply.getData(), serverReply.getLength(),
                        clientPacket.getAddress(), clientPacket.getPort());
                eavesdropSocket.send(replyPacket);
                System.out.println("Relayed reply to client.\n");

                // Note: "halt!" only affects client/server; eavesdropper keeps running.
                if (clientMsg.equals("halt!")) {
                    System.out.println("Received halt command. Client and server may exit, but eavesdropper continues.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (eavesdropSocket != null) eavesdropSocket.close();
            if (forwardSocket != null) forwardSocket.close();
        }
    }
}