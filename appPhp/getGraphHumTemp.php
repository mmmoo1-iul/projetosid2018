<?php
header('Content-Type: application/json; charset=utf-8');

$uid = $_POST['uid'];
$pwd = $_POST['pwd'];
$sql = 'SELECT * FROM DBA.HUMIDADETEMPERATURA';
$db = sasql_connect("DatabaseName = PROJETOSID ; DBF = C:\Users\Mike\Desktop\Productivity\SIDA\Projeto\PROJETOSID.db ; uid = " . $uid . " ; pwd = " . $pwd);
$arr = array();

if(!$db){
	echo "Connection failed!\n";
	die('Não foi possível ligar à base de dados [' . $db->connect_error . ']');
}

$result = sasql_query($db,$sql);
$num_cols = sasql_num_fields($result);
$num_rows = sasql_num_rows($result);
$curr_row = 0;

while($curr_row < $num_rows) {
	$curr_row++;
	$curr_col = 0;
	$col_name = "";
	while( $curr_col < $num_cols ) {
		if($col_name = sasql_fetch_assoc($result)){
			array_push($arr,$col_name);
		}
		$curr_col++;
    }
}

$result = json_encode($arr);
echo $result;
?>