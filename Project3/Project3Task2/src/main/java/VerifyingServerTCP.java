import com.google.gson.Gson;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


/**
 * VerifyingServerTCP.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 17/03/25
 *
 * This server listens on TCP port 7777, maintains a blockchain, and validates
 * incoming client requests by checking the client identifier and digital signature.
 * Each connection remains open to handle multiple requests.
 */

public class VerifyingServerTCP {
    private static BlockChain chainData = new BlockChain();
    private static Gson jsonUtil = new Gson();

    public static void main(String[] args) {
        final int listenPort = 7777;
        System.out.println("Blockchain Verification Server is running.");
        // Initialize blockchain with genesis block
        Block genesisBlock = new Block(0, new Timestamp(System.currentTimeMillis()), "Genesis", 2);
        chainData.addBlock(genesisBlock);

        try (ServerSocket listener = new ServerSocket(listenPort)) {
            while (true) {
                Socket clientConn = listener.accept();
                new Thread(new ConnectionHandler(clientConn)).start();
            }
        } catch (IOException e) {
            System.out.println("Server I/O error: " + e.getMessage());
        }
    }

    // Handles each client connection persistently.
    static class ConnectionHandler implements Runnable {
        private Socket conn;

        public ConnectionHandler(Socket connection) {
            this.conn = connection;
        }

        @Override
        public void run() {
            try (BufferedReader netReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 PrintWriter netWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream())), true)) {

                String incomingJSON;
                while ((incomingJSON = netReader.readLine()) != null) {
                    System.out.println("Received JSON Request:");
                    System.out.println(incomingJSON);
                    RequestMessage requestMsg = jsonUtil.fromJson(incomingJSON, RequestMessage.class);

                    // Validate client identifier
                    if (!validateClientIdentifier(requestMsg)) {
                        respondError(netWriter, "Client identifier does not match public key.");
                        continue;
                    }
                    // Validate digital signature
                    if (!checkSignature(requestMsg)) {
                        respondError(netWriter, "Digital signature verification failed.");
                        continue;
                    }
                    System.out.println("Client public key details:");
                    System.out.println("  Exponent: " + requestMsg.getPublicKeyE());
                    System.out.println("  Modulus: " + requestMsg.getPublicKeyN());
                    System.out.println("Signature validation successful.");

                    ResponseMessage reply = handleRequest(requestMsg);
                    String replyJSON = jsonUtil.toJson(reply);
                    System.out.println("Replying with JSON:");
                    System.out.println(replyJSON);
                    System.out.println("Blockchain size: " + chainData.getChainSize());
                    netWriter.println(replyJSON);
                    netWriter.flush();
                }
                System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            } finally {
                try {
                    if (conn != null) conn.close();
                } catch (IOException ignore) { }
            }
        }
    }

    // Sends an error response.
    private static void respondError(PrintWriter writer, String errorText) {
        ResponseMessage errorReply = new ResponseMessage();
        errorReply.setStatus("error");
        errorReply.setMessage(errorText);
        String errorJSON = jsonUtil.toJson(errorReply);
        writer.println(errorJSON);
        writer.flush();
    }

    // Validates that the provided client identifier matches the last 20 bytes of SHA-256(publicExponent+modulus).
    private static boolean validateClientIdentifier(RequestMessage req) {
        try {
            String combinedKeys = req.getPublicKeyE() + req.getPublicKeyN();
            byte[] hashBytes = computeSHA256(combinedKeys);
            byte[] idFragment = new byte[20];
            System.arraycopy(hashBytes, hashBytes.length - 20, idFragment, 0, 20);
            String calculatedId = convertBytesToHex(idFragment);
            return calculatedId.equals(req.getClientId());
        } catch (Exception e) {
            return false;
        }
    }

    // Checks the digital signature by reconstructing the payload and decrypting the signature.
    private static boolean checkSignature(RequestMessage req) {
        try {
            String payloadStr = assemblePayload(req);
            byte[] payloadHash = computeSHA256(payloadStr);
            BigInteger expectedHash = new BigInteger(1, payloadHash);
            BigInteger signatureVal = new BigInteger(req.getSignature());
            BigInteger pubExponent = new BigInteger(req.getPublicKeyE());
            BigInteger pubModulus = new BigInteger(req.getPublicKeyN());
            BigInteger decryptedSig = signatureVal.modPow(pubExponent, pubModulus);
            return expectedHash.equals(decryptedSig);
        } catch (Exception e) {
            return false;
        }
    }

    // Computes SHA-256 for a given string.
    private static byte[] computeSHA256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes("UTF-8"));
    }

    // Converts a byte array into its hexadecimal string representation.
    private static String convertBytesToHex(byte[] data) {
        StringBuilder hexOut = new StringBuilder();
        for (byte b : data) {
            hexOut.append(String.format("%02x", b));
        }
        return hexOut.toString();
    }

    // Reassembles the payload string from request fields.
    private static String assemblePayload(RequestMessage req) {
        String payload = (req.getClientId() == null ? "" : req.getClientId())
                + (req.getPublicKeyE() == null ? "" : req.getPublicKeyE())
                + (req.getPublicKeyN() == null ? "" : req.getPublicKeyN())
                + (req.getCommand() == null ? "" : req.getCommand());
        if ("addTransaction".equals(req.getCommand())) {
            payload = payload + req.getDifficulty()
                    + (req.getTransaction() == null ? "" : req.getTransaction());
        } else if ("corruptChain".equals(req.getCommand())) {
            payload = payload + req.getBlockIndex()
                    + (req.getNewData() == null ? "" : req.getNewData());
        }
        return payload;
    }

    // Processes the request and returns an appropriate response.
    private static ResponseMessage handleRequest(RequestMessage req) {
        ResponseMessage reply = new ResponseMessage();
        switch (req.getCommand()) {
            case "viewStatus":
                String statusMsg = "Chain size: " + chainData.getChainSize() + "\n" +
                        "Difficulty of latest block: " +
                        (chainData.getLatestBlock() != null ? chainData.getLatestBlock().getDifficulty() : "N/A") + "\n" +
                        "Total chain difficulty: " + chainData.getTotalDifficulty() + "\n" +
                        "Hashes per second: " + chainData.getHashesPerSecond() + "\n" +
                        "Expected total hashes: " + String.format("%.6f", chainData.getTotalExpectedHashes()) + "\n" +
                        "Nonce of latest block: " +
                        (chainData.getLatestBlock() != null ? chainData.getLatestBlock().getNonce() : "N/A") + "\n" +
                        "Chain hash: " + chainData.getChainHash();
                reply.setStatus("success");
                reply.setMessage(statusMsg);
                break;
            case "addTransaction":
                Block newBlock = new Block(chainData.getChainSize(), new Timestamp(System.currentTimeMillis()),
                        req.getTransaction(), req.getDifficulty());
                chainData.addBlock(newBlock);
                reply.setStatus("success");
                reply.setMessage("Transaction added.");
                break;
            case "verifyChain":
                String validity = chainData.isChainValid();
                reply.setStatus(validity.equals("TRUE") ? "success" : "error");
                reply.setMessage("Chain verification: " + validity);
                break;
            case "viewBlockchain":
                reply.setStatus("success");
                reply.setMessage(chainData.toString());
                break;
            case "corruptChain":
                int index = req.getBlockIndex();
                if (index >= 0 && index < chainData.getChainSize()) {
                    chainData.getBlock(index).setData(req.getNewData());
                    reply.setStatus("success");
                    reply.setMessage("Block " + index + " now contains: " + req.getNewData());
                } else {
                    reply.setStatus("error");
                    reply.setMessage("Invalid block index.");
                }
                break;
            case "repairChain":
                chainData.repairChain();
                reply.setStatus("success");
                reply.setMessage("Blockchain repaired.");
                break;
            default:
                reply.setStatus("error");
                reply.setMessage("Unknown command.");
        }
        return reply;
    }
}
