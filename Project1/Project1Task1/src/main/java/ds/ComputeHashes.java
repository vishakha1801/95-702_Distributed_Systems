package ds;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/ComputeHashes")
public class ComputeHashes extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve input text and selected hash function from the form
        String inputText = request.getParameter("inputText");
        String hashFunction = request.getParameter("hashFunction");

        String hexHash = null;
        String base64Hash = null;

        try {
            // Compute the hash
            MessageDigest messageDigest = MessageDigest.getInstance(hashFunction);
            byte[] hashBytes = messageDigest.digest(inputText.getBytes());

            // Convert to hexadecimal and Base64 formats
            hexHash = DatatypeConverter.printHexBinary(hashBytes);
            base64Hash = DatatypeConverter.printBase64Binary(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new ServletException("Invalid hash algorithm selected: " + hashFunction, e);
        }

        // Generate response HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Hash Result</title></head>");
        out.println("<body>");
        out.println("<h1>Hash Result</h1>");
        out.println("<p><strong>Original Text:</strong> " + inputText + "</p>");
        out.println("<p><strong>Hash Function:</strong> " + hashFunction + "</p>");
        out.println("<p><strong>Hexadecimal Hash:</strong> " + hexHash + "</p>");
        out.println("<p><strong>Base64 Hash:</strong> " + base64Hash + "</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
