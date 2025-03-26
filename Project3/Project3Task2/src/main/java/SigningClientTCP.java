import com.google.gson.Gson;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * SigningClientTCP.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 17/03/25
 *
 * This client prompts the user for an RSA key size, generates new RSA keys,
 * displays the RSA parameters and computes a client identifier (the last 20 bytes
 * of SHA-256(publicExponent+modulus)). It then shows a menu (identical to Task 1)
 * and sends digitally signed JSON requests to the server.
 */
public class SigningClientTCP {
    private static final Gson jsonUtil = new Gson();
    // RSA key components renamed
    private static BigInteger rsaPublicExponent, rsaPrivateExponent, rsaModulus;
    private static String clientIdentifier; // computed from public key

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter desired RSA key size (e.g., 512, 1024, 2048): ");
        int keySize = Integer.parseInt(inputScanner.nextLine());
        initRSAKeys(keySize);

        // Display RSA key details
        System.out.println("RSA Public Key:");
        System.out.println("  Exponent: " + rsaPublicExponent);
        System.out.println("  Modulus: " + rsaModulus);
        System.out.println("RSA Private Key:");
        System.out.println("  Exponent: " + rsaPrivateExponent);
        System.out.println("  Modulus: " + rsaModulus);
        System.out.println("Client Identifier: " + clientIdentifier);

        final String serverHost = "localhost";
        final int serverPort = 7777;
        try (Socket connection = new Socket(serverHost, serverPort);
             BufferedReader netReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             PrintWriter netWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)) {

            boolean exitLoop = false;
            while (!exitLoop) {
                // Show menu options
                System.out.println("\n0. View basic blockchain status.");
                System.out.println("1. Add a transaction to the blockchain.");
                System.out.println("2. Verify the blockchain.");
                System.out.println("3. View the blockchain.");
                System.out.println("4. Corrupt the chain.");
                System.out.println("5. Hide the corruption by repairing the chain.");
                System.out.println("6. Quit");
                System.out.print("Enter your selection: ");
                int userChoice = Integer.parseInt(inputScanner.nextLine());

                RequestMessage request = new RequestMessage();
                switch (userChoice) {
                    case 0:
                        request.setCommand("viewStatus");
                        break;
                    case 1:
                        request.setCommand("addTransaction");
                        System.out.print("Enter difficulty > 1: ");
                        request.setDifficulty(Integer.parseInt(inputScanner.nextLine()));
                        System.out.print("Enter transaction details: ");
                        request.setTransaction(inputScanner.nextLine());
                        break;
                    case 2:
                        request.setCommand("verifyChain");
                        break;
                    case 3:
                        request.setCommand("viewBlockchain");
                        break;
                    case 4:
                        request.setCommand("corruptChain");
                        System.out.print("Enter block index to tamper with: ");
                        request.setBlockIndex(Integer.parseInt(inputScanner.nextLine()));
                        System.out.print("Enter new block data for index " + request.getBlockIndex() + ": ");
                        request.setNewData(inputScanner.nextLine());
                        break;
                    case 5:
                        request.setCommand("repairChain");
                        break;
                    case 6:
                        exitLoop = true;
                        continue;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        continue;
                }
                // Set common fields for signing
                request.setClientId(clientIdentifier);
                request.setPublicKeyE(rsaPublicExponent.toString());
                request.setPublicKeyN(rsaModulus.toString());
                // Assemble and sign payload
                String assembledPayload = assemblePayload(request);
                request.setSignature(createSignature(assembledPayload));

                // Transmit JSON request
                String jsonRequest = jsonUtil.toJson(request);
                netWriter.println(jsonRequest);
                netWriter.flush();

                // Await and display JSON response from the server
                String jsonResponse = netReader.readLine();
                if (jsonResponse == null) {
                    System.out.println("No response from server; connection may have terminated.");
                    break;
                }
                ResponseMessage response = jsonUtil.fromJson(jsonResponse, ResponseMessage.class);
                System.out.println(response.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Network I/O Exception: " + e.getMessage());
        }
    }

    // Initializes RSA key components and computes client identifier.
    private static void initRSAKeys(int bits) {
        SecureRandom randomSource = new SecureRandom();
        BigInteger prime1 = BigInteger.probablePrime(bits / 2, randomSource);
        BigInteger prime2 = BigInteger.probablePrime(bits / 2, randomSource);
        rsaModulus = prime1.multiply(prime2);
        BigInteger totient = (prime1.subtract(BigInteger.ONE)).multiply(prime2.subtract(BigInteger.ONE));
        rsaPublicExponent = new BigInteger("65537");
        rsaPrivateExponent = rsaPublicExponent.modInverse(totient);
        // Calculate clientIdentifier as the last 20 bytes of SHA-256(publicExponent+modulus)
        String combinedKeys = rsaPublicExponent.toString() + rsaModulus.toString();
        byte[] hashResult = computeSHA256(combinedKeys);
        byte[] idSegment = new byte[20];
        System.arraycopy(hashResult, hashResult.length - 20, idSegment, 0, 20);
        clientIdentifier = convertBytesToHex(idSegment);
    }

    // Computes SHA-256 hash as a byte array.
    private static byte[] computeSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Converts a byte array into a hex string.
    private static String convertBytesToHex(byte[] byteArray) {
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : byteArray) {
            hexBuilder.append(String.format("%02x", b));
        }
        return hexBuilder.toString();
    }

    // Assembles the payload string from the request fields (order must match server verification).
    private static String assemblePayload(RequestMessage req) {
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append(req.getClientId() == null ? "" : req.getClientId());
        payloadBuilder.append(req.getPublicKeyE() == null ? "" : req.getPublicKeyE());
        payloadBuilder.append(req.getPublicKeyN() == null ? "" : req.getPublicKeyN());
        payloadBuilder.append(req.getCommand() == null ? "" : req.getCommand());
        if ("addTransaction".equals(req.getCommand())) {
            payloadBuilder.append(req.getDifficulty());
            payloadBuilder.append(req.getTransaction() == null ? "" : req.getTransaction());
        } else if ("corruptChain".equals(req.getCommand())) {
            payloadBuilder.append(req.getBlockIndex());
            payloadBuilder.append(req.getNewData() == null ? "" : req.getNewData());
        }
        return payloadBuilder.toString();
    }

    // Creates a digital signature by raising the SHA-256 hash of the payload to the RSA private exponent modulo modulus.
    private static String createSignature(String payload) {
        try {
            byte[] digestBytes = computeSHA256(payload);
            BigInteger digestInteger = new BigInteger(1, digestBytes);
            BigInteger signatureValue = digestInteger.modPow(rsaPrivateExponent, rsaModulus);
            return signatureValue.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
