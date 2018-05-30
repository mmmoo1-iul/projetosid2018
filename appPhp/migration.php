<?php
header('Content-Type: text/plain; charset=utf-8');

$uid = $_POST['uid'];
$pwd = $_POST['pwd'];
$dbsql = 'SELECT * FROM DBA.' . $_POST['db'];
$db = sasql_connect("DatabaseName = PROJETOSID ; DBF = C:\Users\Mike\Desktop\Productivity\SIDA\Projeto\PROJETOSID.db ; uid = " . $uid . " ; pwd = " . $pwd);
if(! $db){
	echo "Connection failed!\n";
	die('Não foi possível ligar à base de dados [' . $db->connect_error . ']');
}
echo 'WORKED';

sasql_close($db);
?>