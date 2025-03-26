package controller;

import model.ClickerModel;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/submit", "/getResults"})
public class ClickerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize ClickerModel and store it in application scope
        ClickerModel model = new ClickerModel();
        getServletContext().setAttribute("clickerModel", model);
        System.out.println("ClickerModel initialized and stored in application scope.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String answer = request.getParameter("answer");
        ClickerModel model = (ClickerModel) getServletContext().getAttribute("clickerModel");

        if (answer != null && !answer.isEmpty()) {
            model.submitAnswer(answer);
            request.setAttribute("selectedAnswer", answer);
        } else {
            request.setAttribute("selectedAnswer", "No answer selected");
        }
        request.setAttribute("results", model.getResults());
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClickerModel model = (ClickerModel) getServletContext().getAttribute("clickerModel");
        Map<String, Integer> results = model.getResults();

        if (results.isEmpty()) {
            System.out.println("Results are empty."); // Debugging log
        } else {
            System.out.println("Results: " + results); // Debugging log
        }

        request.setAttribute("results", results);
        request.getRequestDispatcher("results.jsp").forward(request, response);
    }
}
