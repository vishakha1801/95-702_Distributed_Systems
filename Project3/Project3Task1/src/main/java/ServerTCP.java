/**
 * ServerTCP.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * This class implements the server side of the blockchain simulator.
 * It listens for client connections on a TCP socket, receives JSON request messages,
 * processes them by interacting with a blockchain, and sends JSON response messages back to the client.
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model
 */

import java.net.*;
import java.io.*;
import com.google.gson.Gson;

public class ServerTCP {
    public static void main(String[] args) {
        int serverPort = 7777;
        Gson gson = new Gson();
        // Instantiate the blockchain using your Task 0 implementation.
        BlockChain blockchain = new BlockChain();

        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("Blockchain server running on port " + serverPort + "...");
            while (true) {
                // Accept a new client connection
                Socket clientSocket = listenSocket.accept();
                System.out.println("We have a visitor");

                // Handle the client’s requests sequentially.
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true)) {

                    String jsonRequest;
                    while ((jsonRequest = in.readLine()) != null) {
                        // Display the JSON request received from the client.
                        System.out.println("THE JSON REQUEST MESSAGE IS: " + jsonRequest);

                        // Deserialize the request message.
                        RequestMessage req = gson.fromJson(jsonRequest, RequestMessage.class);
                        ResponseMessage resp = new ResponseMessage();

                        // Process the request based on the action.
                        String action = req.getAction();
                        switch (action) {
                            case "view_status":
                                String status = "Current size of chain: " + blockchain.getChainSize() + "\n" +
                                        "Difficulty of most recent block: " + blockchain.getLatestBlock().getDifficulty() + "\n" +
                                        "Total difficulty for all blocks: " + blockchain.getTotalDifficulty() + "\n" +
                                        "Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond() + "\n" +
                                        "Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes() + "\n" +
                                        "Nonce for most recent block: " + blockchain.getLatestBlock().getNonce() + "\n" +
                                        "Chain hash: " + blockchain.getChainHash();
                                resp.setResponse(status);
                                break;
                            case "add_transaction":
                                long startTime = System.currentTimeMillis();
                                Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), req.getTransaction(), req.getDifficulty());
                                blockchain.addBlock(newBlock);
                                long endTime = System.currentTimeMillis();
                                resp.setResponse("Block added. Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                                break;
                            case "verify_chain":
                                long verifyStart = System.currentTimeMillis();
                                String validity = blockchain.isChainValid();
                                long verifyEnd = System.currentTimeMillis();
                                resp.setResponse("Chain verification: " + validity + "\nTotal execution time required to verify the chain was " + (verifyEnd - verifyStart) + " milliseconds");
                                break;
                            case "view_chain":
                                resp.setResponse("View the Blockchain");
                                resp.setChain(blockchain.toString());
                                break;
                            case "corrupt_chain":
                                int blockID = req.getBlockID();
                                blockchain.getBlock(blockID).setData(req.getNewData());
                                resp.setResponse("Block " + blockID + " now holds " + req.getNewData());
                                break;
                            case "repair_chain":
                                blockchain.repairChain();
                                resp.setResponse("Blockchain repaired.");
                                break;
                            case "exit":
                                resp.setResponse("Exiting client session.");
                                // Send response and close this client connection.
                                String jsonRespExit = gson.toJson(resp);
                                System.out.println("THE JSON RESPONSE MESSAGE IS: " + jsonRespExit);
                                System.out.println("Number of Blocks on Chain == " + blockchain.getChainSize());
                                out.println(jsonRespExit);
                                out.flush();
                                clientSocket.close();
                                break;
                            default:
                                resp.setResponse("Unknown action.");
                        }

                        // If the action was not "exit", serialize and send the response.
                        if (!action.equals("exit")) {
                            String jsonResp = gson.toJson(resp);
                            System.out.println("THE JSON RESPONSE MESSAGE IS: " + jsonResp);
                            System.out.println("Number of Blocks on Chain == " + blockchain.getChainSize());
                            out.println(jsonResp);
                            out.flush();
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Client connection error: " + e.getMessage());
                }
                // Continue to accept new client connections.
            }
        } catch (IOException e) {
            System.out.println("Server IO Exception: " + e.getMessage());
        }
    }
}
