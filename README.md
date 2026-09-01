# SiCA — Sistema de Compartilhamento de Arquivos

Aplicação cliente-servidor desenvolvida em **Java** para realizar o compartilhamento de arquivos através de uma conexão de rede utilizando **Sockets TCP**.

Projeto desenvolvido como exercício prático da disciplina de programação, com o objetivo de implementar a comunicação entre um cliente e um servidor para envio, listagem e download de arquivos.

## 📌 Sobre o projeto

O SiCA (Sistema de Compartilhamento de Arquivos) permite que um cliente estabeleça uma conexão com um servidor e realize operações relacionadas ao armazenamento e transferência de arquivos.

A comunicação entre as aplicações é realizada utilizando o protocolo **TCP**, por meio das classes `Socket` e `ServerSocket` da linguagem Java.

O servidor mantém os arquivos recebidos em uma pasta própria, enquanto o cliente possui um menu através do qual o usuário pode escolher a operação que deseja realizar.

## ⚙️ Funcionalidades

O sistema possui as seguintes funcionalidades:

* 📤 **Enviar arquivo:** permite enviar um arquivo do computador do cliente para o servidor.
* 📋 **Listar arquivos:** exibe os arquivos atualmente armazenados no servidor.
* 📥 **Baixar arquivo:** permite solicitar um arquivo do servidor e salvá-lo no computador do cliente.
* 🚪 **Encerrar conexão:** finaliza a comunicação entre cliente e servidor.

## 🛠️ Tecnologias utilizadas

* **Java**
* **Sockets TCP**
* `ServerSocket`
* `Socket`
* `DataInputStream`
* `DataOutputStream`
* `FileInputStream`
* `FileOutputStream`
* `Scanner`
* IntelliJ IDEA

## 🏗️ Estrutura do projeto

```text
SiCA-Java-Projeto/
│
├── src/
│   ├── Main.java
│   │
│   ├── cliente/
│   │   └── Cliente.java
│   │
│   └── servidor/
│       └── Servidor.java
│
├── .gitignore
│
└── README.md
```

Durante a execução, o sistema também utiliza as pastas:

```text
arquivos_servidor/
```

para armazenar os arquivos recebidos pelo servidor, e:

```text
downloads/
```

para armazenar os arquivos baixados pelo cliente.

Essas pastas são geradas durante a execução e não fazem parte do código-fonte versionado no GitHub.

## 🔌 Funcionamento

### Servidor

O servidor utiliza um `ServerSocket` para aguardar conexões na porta **5000**.

Quando o cliente se conecta, o servidor cria os fluxos de entrada e saída necessários para realizar a comunicação.

O servidor recebe comandos enviados pelo cliente e executa a operação correspondente:

```text
ENVIAR
LISTAR
BAIXAR
SAIR
```

Cada comando direciona a execução para um método responsável pela operação solicitada.

### Cliente

O cliente estabelece uma conexão com o servidor utilizando:

```text
localhost:5000
```

Após a conexão, é apresentado um menu ao usuário:

```text
--- MENU SiCA ---
1 - Enviar arquivo
2 - Listar arquivos do servidor
3 - Baixar arquivo
4 - Sair
```

A escolha do usuário determina qual comando será enviado ao servidor.

## 📤 Envio de arquivo

Ao selecionar a opção **1**, o cliente solicita o caminho do arquivo que será enviado.

O cliente envia ao servidor:

1. O comando `ENVIAR`;
2. O nome do arquivo;
3. O tamanho do arquivo;
4. Os dados do arquivo em blocos de bytes.

O servidor recebe essas informações e grava o arquivo na pasta `arquivos_servidor`.

Após finalizar o recebimento, o servidor envia uma confirmação ao cliente.

## 📋 Listagem de arquivos

Ao selecionar a opção **2**, o cliente envia o comando `LISTAR`.

O servidor verifica os arquivos existentes em sua pasta de armazenamento e envia ao cliente a quantidade de arquivos e seus respectivos nomes.

O cliente então apresenta a lista na tela.

## 📥 Download de arquivo

Ao selecionar a opção **3**, o cliente informa o nome do arquivo que deseja baixar.

O servidor verifica se o arquivo existe.

Caso exista, o servidor envia:

1. Uma confirmação de que o arquivo foi encontrado;
2. O tamanho do arquivo;
3. Os dados do arquivo.

O cliente recebe os dados e salva o arquivo na pasta `downloads`.

## 🔄 Comunicação TCP

A comunicação utiliza o protocolo **TCP**, que estabelece uma conexão entre cliente e servidor antes da transferência dos dados.

No projeto, essa comunicação é realizada por meio das classes:

* `ServerSocket`: responsável por criar o servidor e aguardar conexões.
* `Socket`: responsável pela conexão entre cliente e servidor.
* `DataInputStream`: utilizado para receber dados.
* `DataOutputStream`: utilizado para enviar dados.

Os arquivos são transferidos utilizando buffers de bytes, permitindo que o conteúdo seja enviado e recebido em blocos.

## 🧪 Testes realizados

Foram realizados testes das principais funcionalidades do sistema.

### Envio

Um arquivo chamado `teste.txt` foi enviado pelo cliente para o servidor.

**Resultado:** arquivo enviado com sucesso.

### Listagem

Após o envio, foi utilizada a opção de listagem.

**Resultado:**

```text
1 - teste.txt
```

### Download

Em seguida, o arquivo `teste.txt` foi solicitado pelo cliente através da opção de download.

O arquivo foi salvo na pasta `downloads`.

**Resultado:** arquivo baixado com sucesso.

Por fim, o conteúdo do arquivo baixado foi conferido e estava correto.

## 🎯 Objetivo acadêmico

O projeto foi desenvolvido com o objetivo de aplicar conceitos de programação em rede, comunicação cliente-servidor, utilização de sockets TCP, manipulação de arquivos e transmissão de dados através de uma conexão de rede.

## 👩‍💻 Autora

**Gisela Fogaça Duarte**

Projeto desenvolvido para fins acadêmicos no curso de **Análise e Desenvolvimento de Sistemas (ADS)**.
