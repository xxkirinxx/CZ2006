<?php
$servername = "localhost";  //put url of server if we do actually have a server online
$username = "root";
$password = "";
$dbname = "mozzielogin";

//user submitted variables
$loginUser = $_POST["username"];
$loginPass = $_POST["password"];
$loginPass2 = $_POST["cpassword"];

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT username FROM userslogin WHERE username = '". $loginUser. "'";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
  //username is already taken
  echo "1";
} 
elseif (strcmp($loginPass, $loginPass2) !== 0) {
    //pw and cfm pw entered are different
    echo "2";
}
else {
  $sql2 = "INSERT INTO userslogin (username, password) VALUES ('". $loginUser. "', '". $loginPass. "')";

  if ($conn->query($sql2) === TRUE) {
    echo "3";
  } else {
    echo "Error: " . $sql2 . "<br>" . $conn->error;
    }
}

$conn->close();
?>
