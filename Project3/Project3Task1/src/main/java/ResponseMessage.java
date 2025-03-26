/**
 * ResponseMessage.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * This class encapsulates a JSON response message sent from the server to the client.
 * It includes a response message and, optionally, additional data such as the blockchain state.
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model.
 */

public class ResponseMessage {
    private String response; // The primary text message to be displayed to the client.
    private String chain;    // Optional: A JSON-like representation of the blockchain.
    private int numBlocks;   // Optional: The number of blocks on the blockchain.

    // Default constructor required for Gson
    public ResponseMessage() {}

    // Parameterized constructors
    public ResponseMessage(String response) {
        this.response = response;
    }

    public ResponseMessage(String response, String chain, int numBlocks) {
        this.response = response;
        this.chain = chain;
        this.numBlocks = numBlocks;
    }

    // Getters and setters
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }

    public String getChain() {
        return chain;
    }
    public void setChain(String chain) {
        this.chain = chain;
    }

    public int getNumBlocks() {
        return numBlocks;
    }
    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }
}
