<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("parkName") %></title>
    <style>
        body {
            font-family: 'Roboto', Calibri, sans-serif;
            line-height: 1.8;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
            color: #333;
        }
        h1 {
            text-align: center;
            font-size: 2.2em;
            margin-top: 20px;
            color: #2c3e50;
        }
        h2 {
            font-size: 1.8em;
            color: #34495e;
            margin-bottom: 10px;
        }
        .image-box {
            display: flex;
            justify-content: center;
            align-items: center;
            border: 2px solid #ddd;
            border-radius: 12px;
            padding: 15px;
            max-width: 85%;
            margin: 20px auto;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .image-box img {
            max-width: 100%;
            max-height: 500px;
            object-fit: cover;
            border-radius: 8px;
        }
        .content {
            max-width: 850px;
            margin: 20px auto;
            padding: 15px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .bold {
            font-weight: bold;
            color: #2c3e50;
        }
        p {
            margin: 10px 0;
            font-size: 1.1em;
        }
        @media (max-width: 768px) {
            h1 {
                font-size: 1.8em;
            }
            h2 {
                font-size: 1.5em;
            }
            .image-box {
                max-width: 95%;
            }
            .content {
                max-width: 95%;
            }
        }
    </style>
</head>
<body>

<h1><%= request.getAttribute("parkName") %></h1>

<!-- Park Image -->
<div class="image-box">
    <img src="<%= request.getAttribute("parkImage") %>" alt="Park Image">
</div>
<!-- Weather Conditions -->
<div class="content">
    <h2>Weather Conditions:</h2>
    <p><span class="bold">Current conditions:</span><br>
        <%= request.getAttribute("weather") %></p>
    <p><span class="bold">Credit:</span> forecast.weather.gov (National Weather Service)</p>
</div>

<!-- Park Activities -->
<div class="content">
    <h2>Activities:</h2>
    <p><%= request.getAttribute("activities") %></p>
    <p><span class="bold">Credit:</span> https://developer.nps.gov</p>
</div>

</body>
</html>
