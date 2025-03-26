<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>U.S. National Parks</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f9;
            text-align: center;
            margin: 0;
            padding: 20px;
            color: #2c3e50;
        }
        h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
            color: #34495e;
        }
        h3 {
            font-size: 1.2em;
            margin-bottom: 20px;
            color: #555;
        }
        form {
            background: #fff;
            padding: 20px;
            max-width: 500px;
            margin: 0 auto;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        label {
            font-size: 1.1em;
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
            color: #2c3e50;
        }
        select {
            width: 100%;
            padding: 10px;
            font-size: 1em;
            border: 1px solid #ccc;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        button {
            background-color: #3498db;
            color: white;
            font-size: 1.2em;
            padding: 10px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background 0.3s ease;
        }
        button:hover {
            background-color: #2980b9;
        }
        @media (max-width: 600px) {
            form {
                max-width: 90%;
                padding: 15px;
            }
            h1 {
                font-size: 2em;
            }
            button {
                width: 100%;
            }
        }
    </style>
</head>
<body>

<h1>U.S. National Parks</h1>
<h3>Created by: Vishakha Pathak</h3>

<form action="nationalpark" method="post">
    <label for="park">Choose a park:</label>
    <select name="park" id="park" required>
        <option value="acad" data-lat="44.3962" data-lon="-68.2246" data-name="Acadia National Park">Acadia NP</option>
        <option value="cuva" data-lat="41.0969" data-lon="-81.4611" data-name="Cuyahoga Valley National Park">Cuyahoga Valley NP</option>
        <option value="grsm" data-lat="35.726" data-lon="-83.482" data-name="Great Smoky Mountains National Park">Great Smoky Mountains NP</option>
        <option value="maca" data-lat="37.1246" data-lon="-86.0968" data-name="Mammoth Cave National Park">Mammoth Cave NP</option>
        <option value="neri" data-lat="37.9263" data-lon="-81.1547" data-name="New River Gorge National Park">New River Gorge NP</option>
        <option value="shen" data-lat="38.6633" data-lon="-78.4635" data-name="Shenandoah National Park">Shenandoah NP</option>
    </select>
    <input type="hidden" name="lat" id="lat">
    <input type="hidden" name="lon" id="lon">
    <input type="hidden" name="parkName" id="parkName">
    <button type="submit">Submit</button>
</form>

<script>
    const dropdown = document.getElementById("park");
    dropdown.addEventListener("change", function () {
        const selectedOption = dropdown.options[dropdown.selectedIndex];
        document.getElementById("lat").value = selectedOption.getAttribute("data-lat");
        document.getElementById("lon").value = selectedOption.getAttribute("data-lon");
        document.getElementById("parkName").value = selectedOption.getAttribute("data-name");
    });
    dropdown.dispatchEvent(new Event("change"));
</script>

</body>
</html>
