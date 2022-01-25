#JSON-DATABASE#

###Descrição###
Esta aplicação consiste em dois programas, um cliente e um servidor. Ambos comunicam-se através de uma *socket* de endereço fixado.
O cliente recebe os dados a serem direcionados para o servidor, em forma de chave e valor. O servidor, por sua vez, organiza os dados
em forma com base nos formatos JsonObject, JsonArray e JsonPrimitive (formatos amplamente conhecidos, mas aqui emprestados da lib Gson).

A persistência dos dados é feita pelo servidor salvando os dados enviados em formato de arquivo .json.

###RELEASE 0.0.1###
Parâmetros recebidos pelo cliente são enviados ou em forma de parâmetros na IDE (lib Beust), ou na forma de arquivo Json. **Projeto para versão 0.1.0**: Alterar para envio via frontend java-script.

Persistência em formato de arquivo .json hospeadado localmente. **Projeto para versão 0.1.0**: Alterar persistência para arquivo hospedado na nuvem.

**Projeto para versão 0.1.0**: Hospedar cliente e servidor como duas aplicações rodando em um container na nuvem, com frontend javascript comunicando com o servidor.