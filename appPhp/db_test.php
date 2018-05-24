<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>Teste de ligação a base de dados em PHP com o mysqli</title>
	</head>

	<body>

		<?php
			// Ligação à BD: mysqli('servidor', 'utilizador', 'password', 'nome_bd')
			$db = new mysqli('127.0.0.1', 'test_user', 'test_password', 'php_tests');

			// Testar a ligação
			if($db->connect_errno > 0){ // Código de erro acima de 0, significa que houve problemas
				// Terminar a execução com mensagem de erro
		    	die('Não foi possível ligar à base de dados [' . $db->connect_error . ']');
			}

			// Mudar o charset para UTF8 - isto é importante se a BD também está em UTF8, porque senão os acentos aparecem com caracteres esquisitos
			$db->set_charset("utf8");

			// Construir uma query simples
			$sql = "SELECT * FROM user";

			// Correr a query
			if(!$result = $db->query($sql)){ // Se ocorreu um erro
				// Terminar a execução com mensagem de erro
			    die('Não foi possível executar a query [' . $db->error . ']');
			}

			// Mostrar o número de resultados
			echo 'Número de resultados: ' . $result->num_rows . '<br/><br/>';

			?>

				<form action="db_test2.php" method="post">
					<select name="user_id" id="user_id">
						<?php
						// Iterar sobre os resultados
						while($row = $result->fetch_assoc()){ // Neste exemplo, usa-se um array associativo dos resultados
							?>
								<option value="<?php echo $row['id']; ?>"><?php echo $row['username']; ?></option>
						    <?php
						}
						?>
					</select>

					<button type="submit">Enviar</button>
				</form>
			<?php

			// Importante para libertar recursos de memória
			$result->free();

			// Depois de feito, fechar a ligação à BD
			$db->close();
		?>
	</body>
</html>
