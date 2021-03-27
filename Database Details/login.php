<?php
$servername = "localhost";  //put url of server if we do actually have a server online
$username = "root";
$password = "";
$dbname = "mozzielogin";

//user submitted variables
$loginUser = $_POST["username"];
$loginPass = $_POST["password"];

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT password FROM userslogin WHERE username = '". $loginUser. "'";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
  // output data of each row
  while($row = $result->fetch_assoc()) {
    if ($row["password"] == $loginPass) {
        echo "1"; //success
    }
    else {
        echo "2"; //password wrong
    }
  }
} else {
  echo "3"; //username does not exist
}

$conn->close();
?>