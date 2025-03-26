/**
 * ClientTCP.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * This class implements the client side of the blockchain simulator.
 * It presents a menu similar to Task 0, but communicates with the server via JSON messages over TCP sockets.
 * By default, it connects to localhost on port 7777.
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;
import com.google.gson.Gson;

public class ClientTCP {
    public static void main(String[] args) {
        // Use localhost by default; if an argument is provided, use it as the server hostname.
        String serverHostname = "localhost";
        if (args.length >= 1) {
            serverHostname = args[0];
        }

        int serverPort = 7777;
        Gson gson = new Gson();

        try (Socket socket = new Socket(serverHostname, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
             Scanner scanner = new Scanner(System.in)) {

            boolean exit = false;
            while (!exit) {
                // Display menu
                System.out.println("0. View basic blockchain status.");
                System.out.println("1. Add a transaction to the blockchain.");
                System.out.println("2. Verify the blockchain.");
                System.out.println("3. View the blockchain.");
                System.out.println("4. Corrupt the chain.");
                System.out.println("5. Hide the corruption by recomputing hashes.");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                RequestMessage req = new RequestMessage();
                switch (choice) {
                    case 0:
                        req.setAction("view_status");
                        break;
                    case 1:
                        req.setAction("add_transaction");
                        System.out.print("Enter difficulty > 1: ");
                        int diff = scanner.nextInt();
                        scanner.nextLine();
                        req.setDifficulty(diff);
                        System.out.print("Enter transaction: ");
                        String transaction = scanner.nextLine();
                        req.setTransaction(transaction);
                        break;
                    case 2:
                        req.setAction("verify_chain");
                        break;
                    case 3:
                        req.setAction("view_chain");
                        break;
                    case 4:
                        req.setAction("corrupt_chain");
                        System.out.print("Enter block ID of block to corrupt: ");
                        int blockID = scanner.nextInt();
                        scanner.nextLine();
                        req.setBlockID(blockID);
                        System.out.print("Enter new data for block " + blockID + ": ");
                        String newData = scanner.nextLine();
                        req.setNewData(newData);
                        break;
                    case 5:
                        req.setAction("repair_chain");
                        break;
                    case 6:
                        req.setAction("exit");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue; // skip sending a request for invalid input
                }

                // Convert RequestMessage to JSON and send it to the server
                String jsonRequest = gson.toJson(req);
                out.println(jsonRequest);

                // Read response from server
                String jsonResponse = in.readLine();
                if (jsonResponse == null) {
                    System.out.println("Server closed connection.");
                    break;
                }
                // Deserialize response message
                ResponseMessage resp = gson.fromJson(jsonResponse, ResponseMessage.class);
                // Display the response message and blockchain status if available
                System.out.println("Server Response: " + resp.getResponse());
                if (resp.getChain() != null && !resp.getChain().isEmpty()) {
                    System.out.println("Blockchain: " + resp.getChain());
                }
                System.out.println("Number of Blocks on Chain == " + resp.getNumBlocks());
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
