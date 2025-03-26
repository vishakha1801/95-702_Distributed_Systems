<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Distributed Systems Class Clicker</title>

  <!-- Internal CSS for styling -->
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
      background-color: #f4f4f4;
      color: #333;
    }

    h2, h3 {
      color: #4CAF50;
      text-align: center;
    }

    .container {
      width: 80%;
      margin: 0 auto;
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .form-section {
      margin-bottom: 20px;
    }

    label {
      font-weight: bold;
    }

    .answer-options {
      margin: 10px 0;
    }

    .answer-options input {
      margin-right: 10px;
    }

    button {
      background-color: #4CAF50;
      color: white;
      border: none;
      padding: 10px 20px;
      cursor: pointer;
      border-radius: 4px;
    }

    button:hover {
      background-color: #45a049;
    }

    .feedback {
      background-color: #e7f7e7;
      padding: 10px;
      border-radius: 4px;
      margin-bottom: 20px;
    }

    .results-link {
      display: block;
      text-align: center;
      font-size: 18px;
      margin-top: 20px;
      text-decoration: none;
      color: #4CAF50;
    }

    .results-link:hover {
      text-decoration: underline;
    }

    .result-list {
      list-style-type: none;
      padding: 0;
    }

    .result-item {
      background-color: #e7f7e7;
      margin: 10px 0;
      padding: 10px;
      border-radius: 4px;
    }
  </style>
</head>
<body>

<div class="container">
  <!-- Title/Heading for the page -->
  <h2>The results from the survey are as follows:</h2>

  <!-- Results Section: Display if results are available -->
  <c:if test="${not empty results}">
    <ul class="result-list">
      <c:forEach var="entry" items="${results}">
        <li class="result-item">${entry.key}: ${entry.value} votes</li>
      </c:forEach>
    </ul>
  </c:if>

  <!-- No results available message -->
  <c:if test="${empty results}">
    <p>No results available yet.</p>
  </c:if>

  <!-- Link back to the submission page -->
  <h3><a class="results-link" href="/">Back to Submit Page</a></h3>
</div>

</body>
</html>
