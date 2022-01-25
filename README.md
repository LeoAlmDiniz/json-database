#JSON-DATABASE#

###Descri��o###
Esta aplica��o consiste em dois programas, um cliente e um servidor. Ambos comunicam-se atrav�s de uma *socket* de endere�o fixado.
O cliente recebe os dados a serem direcionados para o servidor, em forma de chave e valor. O servidor, por sua vez, organiza os dados
em forma com base nos formatos JsonObject, JsonArray e JsonPrimitive (formatos amplamente conhecidos, mas aqui emprestados da lib Gson).

A persist�ncia dos dados � feita pelo servidor salvando os dados enviados em formato de arquivo .json.

###RELEASE 0.0.1###
Par�metros recebidos pelo cliente s�o enviados ou em forma de par�metros na IDE (lib Beust), ou na forma de arquivo Json. **Projeto para vers�o 0.1.0**: Alterar para envio via frontend java-script.

Persist�ncia em formato de arquivo .json hospeadado localmente. **Projeto para vers�o 0.1.0**: Alterar persist�ncia para arquivo hospedado na nuvem.

**Projeto para vers�o 0.1.0**: Hospedar cliente e servidor como duas aplica��es rodando em um container na nuvem, com frontend javascript comunicando com o servidor.