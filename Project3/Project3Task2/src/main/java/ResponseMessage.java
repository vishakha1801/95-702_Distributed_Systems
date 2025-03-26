/**
 * ResponseMessage.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 17/03/25
 *
 * */

public class ResponseMessage {
    private String status;    // "success" or "error"
    private String message;
    private String chain;     // (optional) JSON-like representation of the blockchain
    private int numBlocks;    // (optional) number of blocks on the chain

    // Default constructor required for Gson
    public ResponseMessage() {}

    // Overloaded constructor for a minimal response
    public ResponseMessage(String message, int numBlocks, boolean verified) {
        this.message = message;
        this.numBlocks = numBlocks;
        // You can choose to leave 'chain' as empty if not provided.
        this.chain = "";
        this.status = verified ? "success" : "error";
    }

    // Full constructor
    public ResponseMessage(String status, String message, String chain, int numBlocks) {
        this.status = status;
        this.message = message;
        this.chain = chain;
        this.numBlocks = numBlocks;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getChain() { return chain; }
    public int getNumBlocks() { return numBlocks; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setChain(String chain) { this.chain = chain; }
    public void setNumBlocks(int numBlocks) { this.numBlocks = numBlocks; }
}
