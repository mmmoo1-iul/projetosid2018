<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>Teste 2 de ligação a base de dados em PHP com o mysqli</title>
	</head>

	<body>

		<?php
			if(!isset($_POST['user_id'])){
				die('Nada feito, amigo!');
			}

			$user_id = $_POST['user_id'];

			// Ligação à BD: mysqli('servidor', 'utilizador', 'password', 'nome_bd')
			$db = new mysqli('127.0.0.1', 'test_user', 'test_password', 'php_tests');

			// Testar a ligação
			if($db->connect_errno > 0){ // Código de erro acima de 0, significa que houve problemas
				// Terminar a execução com mensagem de erro
		    	die('Não foi possível ligar à base de dados [' . $db->connect_error . ']');
			}

			// Mudar o charset para UTF8 - isto é importante se a BD também está em UTF8, porque senão os acentos aparecem com caracteres esquisitos
			$db->set_charset("utf8");

			// Este passo é muito importante pois ajuda a prevenir ataques de SQL Injection
			$user_id = $db->real_escape_string($user_id); // Limpa caracteres especiais do Mysql como ', ", \

			// Construir uma query simples
			$sql = "SELECT * FROM user WHERE id = " . $user_id;

			// Correr a query
			if(!$result = $db->query($sql)){ // Se ocorreu um erro
				// Terminar a execução com mensagem de erro
			    die('Não foi possível executar a query [' . $db->error . ']');
			}

			// Mostrar o número de resultados
			echo 'Número de resultados: ' . $result->num_rows . '<br/><br/>';

			// Iterar sobre os resultados
			while($row = $result->fetch_assoc()){ // Neste exemplo, usa-se um array associativo dos resultados
				?>
					<label>User ID</label>: <?php echo $row['id']; ?><br>
					<label>Username</label>: <?php echo $row['username']; ?><br>
					<label>Nome</label>: <?php echo $row['name']; ?><br>
			    <?php
			}
			// Importante para libertar recursos de memória
			$result->free();

			// Depois de feito, fechar a ligação à BD
			$db->close();
		?>
	</body>
</html>
