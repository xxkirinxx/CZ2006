<?php
$servername = "localhost";  //put url of server if we do actually have a server online
$username = "root";
$password = "";
$dbname = "mozzielogin";

//user submitted variables
$loginUser = $_POST["email"];
$loginPass = $_POST["newpassword"];

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT password FROM userslogin WHERE username = '". $loginUser. "'";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $sql2 = "UPDATE userslogin SET password = '". $loginPass. "' WHERE username = '". $loginUser. "'";
    $conn->query($sql2);
    echo "2";
} else {
  echo "1"; //username does not exist
}

$conn->close();
?>