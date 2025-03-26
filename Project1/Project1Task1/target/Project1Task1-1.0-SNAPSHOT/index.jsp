<!DOCTYPE html>
<html>
<head>
    <title>Hash Function Calculator</title>
</head>
<body>
<h1>Hash Function Calculator</h1>
<form action="ComputeHashes" method="post">
    <label for="inputText">Enter text:</label><br>
    <textarea id="inputText" name="inputText" rows="4" cols="50"></textarea><br><br>

    <label>Select Hash Function:</label><br>
    <input type="radio" id="md5" name="hashFunction" value="MD5" checked>
    <label for="md5">MD5</label><br>
    <input type="radio" id="sha256" name="hashFunction" value="SHA-256">
    <label for="sha256">SHA-256</label><br><br>

    <button type="submit">Compute Hash</button>
</form>
</body>
</html>
