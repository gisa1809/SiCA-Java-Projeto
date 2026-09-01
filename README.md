# SiCA - Sistema de Compartilhamento de Arquivos

O **SiCA (Sistema de Compartilhamento de Arquivos)** é uma aplicação desenvolvida em **Java** com o objetivo de permitir a transferência de arquivos entre um computador cliente e um servidor por meio de uma conexão de rede.

O projeto utiliza o modelo **cliente-servidor**, no qual o servidor fica responsável por receber as solicitações e armazenar os arquivos, enquanto o cliente fornece ao usuário as opções para enviar, consultar e baixar arquivos.

A comunicação entre as duas partes é realizada utilizando **Sockets TCP**, permitindo que os dados sejam transmitidos através de uma conexão estabelecida entre cliente e servidor.

Este projeto foi desenvolvido como atividade prática do curso de **Análise e Desenvolvimento de Sistemas (ADS) da PUC Goiás**, com o objetivo de aplicar conceitos de programação em rede, comunicação entre aplicações e manipulação de arquivos em Java.

## Sobre o projeto

O funcionamento do SiCA é baseado em duas aplicações independentes: o **Servidor** e o **Cliente**.

O servidor é iniciado primeiro e fica aguardando uma conexão na porta `5000`. Quando o cliente é executado, ele estabelece uma conexão com o servidor utilizando o endereço `localhost` e a mesma porta.

Depois que a conexão é estabelecida, o cliente apresenta um menu ao usuário. A partir desse menu é possível escolher qual operação será realizada.

As solicitações são enviadas para o servidor através da conexão TCP, e o servidor identifica cada operação por meio de comandos específicos.

Atualmente, o sistema trabalha com quatro comandos principais:

* `ENVIAR` - utilizado para enviar um arquivo ao servidor;
* `LISTAR` - utilizado para consultar os arquivos armazenados;
* `BAIXAR` - utilizado para solicitar um arquivo do servidor;
* `SAIR` - utilizado para encerrar a comunicação.

Dessa forma, o cliente e o servidor possuem responsabilidades diferentes, mas trabalham em conjunto durante toda a execução da aplicação.

## Funcionalidades

### Envio de arquivos

A opção **Enviar arquivo** permite selecionar um arquivo existente no computador do cliente e transferi-lo para o servidor.

Antes da transferência, o cliente verifica se o caminho informado corresponde a um arquivo existente. Em seguida, envia para o servidor o comando da operação, o nome do arquivo e o seu tamanho.

Os dados do arquivo são enviados em pequenos blocos de bytes. O servidor recebe esses blocos e os grava em um arquivo dentro da pasta `arquivos_servidor`.

Após concluir a transferência, o servidor envia uma confirmação para o cliente, informando que o arquivo foi recebido.

### Listagem de arquivos

A opção **Listar arquivos do servidor** permite consultar quais arquivos estão atualmente armazenados no servidor.

Quando essa opção é selecionada, o cliente envia o comando `LISTAR`. O servidor verifica o conteúdo da pasta destinada ao armazenamento e identifica os arquivos disponíveis.

A quantidade de arquivos e seus respectivos nomes são enviados de volta para o cliente, que apresenta a lista no terminal.

Caso a pasta não possua arquivos, o sistema informa ao usuário que nenhum arquivo está disponível.

### Download de arquivos

A opção **Baixar arquivo** permite transferir um arquivo que já esteja armazenado no servidor para o computador do cliente.

O cliente informa o nome do arquivo desejado e envia essa informação ao servidor. O servidor verifica se o arquivo existe.

Quando o arquivo é encontrado, seu tamanho e seus dados são enviados para o cliente. O cliente recebe as informações e cria uma cópia do arquivo dentro da pasta `downloads`.

Caso o arquivo solicitado não exista, o servidor informa ao cliente que o arquivo não foi encontrado.

### Encerramento da conexão

A opção **Sair** envia o comando `SAIR` para o servidor.

Ao receber esse comando, o servidor encerra o processamento daquela conexão e os recursos utilizados na comunicação são fechados.

## Comunicação utilizando TCP

A comunicação do projeto utiliza o protocolo **TCP (Transmission Control Protocol)**.

Diferentemente de uma simples troca de informações entre métodos dentro de um mesmo programa, o cliente e o servidor são aplicações separadas e precisam estabelecer uma conexão para trocar dados.

No servidor, a classe `ServerSocket` é utilizada para abrir a porta `5000` e aguardar uma conexão.

Quando o cliente se conecta, é criado um objeto `Socket`, que representa a comunicação entre as duas aplicações.

Para realizar a troca de informações, o projeto utiliza:

* `DataInputStream` para receber dados;
* `DataOutputStream` para enviar dados.

Essas classes também permitem transmitir diferentes tipos de informação, como `String` e `long`, utilizados pelo projeto para enviar comandos, nomes e tamanhos dos arquivos.

Durante a transferência dos arquivos, são utilizados buffers de bytes. Isso permite que arquivos maiores sejam enviados e recebidos em blocos, em vez de precisar carregar todo o conteúdo do arquivo na memória de uma única vez.

## Organização do projeto

O projeto está organizado separando as responsabilidades do cliente e do servidor:

```text
SiCA-Java-Projeto/
├── src/
│   ├── cliente/
│   │   └── Cliente.java
│   │
│   ├── servidor/
│   │   └── Servidor.java
│   │
│   └── Main.java
│
├── .gitignore
└── README.md
```

### Cliente

A classe `Cliente.java` é responsável pela interação com o usuário e pela comunicação com o servidor.

Ela apresenta o menu de operações e possui métodos específicos para:

* enviar arquivos;
* listar arquivos;
* baixar arquivos;
* encerrar a conexão.

### Servidor

A classe `Servidor.java` é responsável por receber as conexões e processar as solicitações enviadas pelo cliente.

Os principais métodos implementados são:

* `receberArquivo()` - recebe e armazena arquivos enviados pelo cliente;
* `listarArquivos()` - consulta os arquivos armazenados no servidor;
* `enviarArquivo()` - realiza o envio de um arquivo solicitado pelo cliente.

Essa divisão permite que cada parte da aplicação tenha uma responsabilidade específica dentro do sistema.

## Armazenamento dos arquivos

Os arquivos enviados para o servidor são armazenados na pasta:

```text
arquivos_servidor/
```

Essa pasta é criada automaticamente pelo servidor caso ainda não exista.

Já os arquivos baixados pelo cliente são armazenados na pasta:

```text
downloads/
```

A pasta de downloads também é criada automaticamente quando necessário.

Essas pastas são utilizadas durante a execução da aplicação e, por isso, não são necessárias no repositório do código-fonte.

## Fluxo de funcionamento

De forma geral, o funcionamento do sistema ocorre da seguinte maneira:

```text
Cliente                         Servidor
   │                               │
   │──── Conexão TCP ─────────────>│
   │                               │
   │──── Comando ENVIAR ──────────>│
   │──── Dados do arquivo ────────>│
   │                               │
   │<──── Confirmação ─────────────│
   │                               │
   │──── Comando LISTAR ──────────>│
   │<──── Lista de arquivos ───────│
   │                               │
   │──── Comando BAIXAR ─────────>│
   │<──── Dados do arquivo ────────│
   │                               │
   │──── Comando SAIR ────────────>│
   │                               │
   └──── Encerramento da conexão ─┘
```

Esse fluxo representa as principais operações implementadas no projeto e demonstra a comunicação bidirecional entre as aplicações.

## Como executar

Para executar o projeto, é necessário iniciar primeiro o servidor.

### 1. Iniciar o servidor

Execute a classe:

```text
servidor.Servidor
```

O servidor será iniciado na porta `5000` e ficará aguardando uma conexão.

### 2. Iniciar o cliente

Depois que o servidor estiver em execução, execute:

```text
cliente.Cliente
```

O cliente tentará estabelecer uma conexão utilizando:

```text
localhost:5000
```

Após a conexão, o menu será apresentado:

```text
--- MENU SiCA ---
1 - Enviar arquivo
2 - Listar arquivos do servidor
3 - Baixar arquivo
4 - Sair
```

A partir desse momento, o usuário pode realizar as operações disponíveis.

## Testes realizados

Durante o desenvolvimento, foram realizados testes das principais operações do sistema.

Um arquivo chamado `teste.txt` foi utilizado para verificar a transferência entre cliente e servidor.

Primeiramente, o arquivo foi enviado pelo cliente utilizando a opção **1 - Enviar arquivo**. O servidor recebeu o arquivo e o armazenou em sua pasta.

Em seguida, foi utilizada a opção **2 - Listar arquivos do servidor**, que apresentou o arquivo `teste.txt` como disponível.

Por fim, o arquivo foi solicitado novamente pelo cliente através da opção **3 - Baixar arquivo**. O sistema realizou a transferência e salvou uma cópia na pasta `downloads`.

Os testes confirmaram o funcionamento das operações de envio, listagem e download implementadas no projeto.

## Tecnologias utilizadas

* **Java**
* **Sockets TCP**
* `ServerSocket`
* `Socket`
* `DataInputStream`
* `DataOutputStream`
* `FileInputStream`
* `FileOutputStream`
* `Scanner`
* **IntelliJ IDEA**
* **Git e GitHub**

## Objetivo acadêmico

O desenvolvimento do SiCA possibilitou a aplicação prática de conceitos relacionados à programação em rede e ao modelo cliente-servidor.

Além da comunicação utilizando sockets TCP, o projeto também trabalha com leitura e escrita de arquivos, transmissão de dados em blocos, tratamento de exceções e organização do código em diferentes classes e pacotes.

## Autora

**Gisela Fogaça Duarte**

Projeto acadêmico desenvolvido no curso de **Análise e Desenvolvimento de Sistemas (ADS) da PUC Goiás**.
