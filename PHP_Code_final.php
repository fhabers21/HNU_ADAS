<?php
  $fb = $_REQUEST['fb'];
  $server = "localhost";
  $user = "root";
  $pw = "";
  $dbname = "fbDB3";
  
  $conn = new mysqli($server, $user, $pw, $dbname);
  
  if($conn->connection_error) {
      die("Connection failed: " . $conn->connect_error);
  }
  $sqlstmt = "SELECT answer from fb WHERE fb='".$fb."'";
  
  $res = $conn->query($sqlstmt);
  
  if ($res->num_rows > 0) {
     while($row = mysqli_fetch_assoc($res)) {
        echo $row["answer"];
    }
  } else {
      echo "We will answer asap!";
  }
  $conn->close();
?>