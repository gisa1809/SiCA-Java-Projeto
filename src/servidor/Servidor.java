package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    // Porta utilizada para a comunicação TCP.
    private static final int PORTA = 5000;

    // Pasta onde os arquivos serão armazenados.
    private static final String PASTA_ARQUIVOS = "arquivos_servidor";

    public static void main(String[] args) {

        // Cria a pasta de arquivos caso ela ainda não exista.
        File pastaArquivos = new File(PASTA_ARQUIVOS);

        if (!pastaArquivos.exists()) {
            pastaArquivos.mkdir();
        }

        System.out.println("=== SERVIDOR SiCA ===");
        System.out.println("Iniciando servidor...");

        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            System.out.println(
                    "Servidor aguardando conexão na porta "
                            + PORTA + "..."
            );

            // O servidor aceita um cliente.
            Socket cliente = servidor.accept();

            System.out.println("Cliente conectado com sucesso!");

            DataInputStream entrada =
                    new DataInputStream(cliente.getInputStream());

            DataOutputStream saida =
                    new DataOutputStream(cliente.getOutputStream());

            boolean conectado = true;

            // Mantém a comunicação enquanto o cliente estiver conectado.
            while (conectado) {

                String comando;

                try {
                    // Recebe o comando enviado pelo cliente.
                    comando = entrada.readUTF();

                } catch (IOException e) {
                    // Caso o cliente encerre a conexão inesperadamente.
                    break;
                }

                System.out.println(
                        "Comando recebido: " + comando
                );

                switch (comando) {

                    case "ENVIAR":

                        receberArquivo(
                                entrada,
                                saida,
                                pastaArquivos
                        );

                        break;

                    case "LISTAR":

                        listarArquivos(
                                saida,
                                pastaArquivos
                        );

                        break;

                    case "BAIXAR":

                        enviarArquivo(
                                entrada,
                                saida,
                                pastaArquivos
                        );

                        break;

                    case "SAIR":

                        System.out.println(
                                "Cliente solicitou encerramento."
                        );

                        conectado = false;
                        break;

                    default:

                        System.out.println(
                                "Comando desconhecido."
                        );

                        saida.writeUTF("ERRO");
                        saida.flush();
                }
            }

            entrada.close();
            saida.close();
            cliente.close();

            System.out.println("Servidor encerrado.");

        } catch (IOException e) {

            System.out.println(
                    "Erro no servidor: " + e.getMessage()
            );
        }
    }

    /**
     * Recebe um arquivo enviado pelo cliente.
     *
     * O cliente envia o nome, o tamanho e os bytes do arquivo.
     * O servidor salva esses dados dentro da pasta
     * arquivos_servidor.
     */
    private static void receberArquivo(
            DataInputStream entrada,
            DataOutputStream saida,
            File pastaArquivos) throws IOException {

        // Recebe o nome do arquivo.
        String nomeArquivo = entrada.readUTF();

        // Recebe o tamanho do arquivo.
        long tamanhoArquivo = entrada.readLong();

        // Cria o arquivo dentro da pasta do servidor.
        File arquivo = new File(
                pastaArquivos,
                nomeArquivo
        );

        System.out.println(
                "Recebendo arquivo: " + nomeArquivo
        );

        try (FileOutputStream arquivoSaida =
                     new FileOutputStream(arquivo)) {

            byte[] buffer = new byte[4096];

            long totalRecebido = 0;

            // Recebe os bytes até completar o arquivo.
            while (totalRecebido < tamanhoArquivo) {

                int bytesParaLer =
                        (int) Math.min(
                                buffer.length,
                                tamanhoArquivo - totalRecebido
                        );

                int bytesLidos = entrada.read(
                        buffer,
                        0,
                        bytesParaLer
                );

                if (bytesLidos == -1) {
                    throw new IOException(
                            "Conexão encerrada durante o envio."
                    );
                }

                arquivoSaida.write(
                        buffer,
                        0,
                        bytesLidos
                );

                totalRecebido += bytesLidos;
            }
        }

        System.out.println(
                "Arquivo recebido com sucesso: "
                        + nomeArquivo
        );

        // Confirma ao cliente que o arquivo foi recebido.
        saida.writeUTF("ARQUIVO_RECEBIDO");
        saida.flush();
    }

    /**
     * Lista todos os arquivos presentes na pasta do servidor
     * e envia os nomes para o cliente.
     */
    private static void listarArquivos(
            DataOutputStream saida,
            File pastaArquivos) throws IOException {

        File[] arquivos = pastaArquivos.listFiles();

        // Caso não existam arquivos.
        if (arquivos == null || arquivos.length == 0) {

            saida.writeUTF("NENHUM_ARQUIVO");

            saida.flush();

            return;
        }

        // Conta somente arquivos, ignorando possíveis pastas.
        int quantidade = 0;

        for (File arquivo : arquivos) {

            if (arquivo.isFile()) {
                quantidade++;
            }
        }

        // Envia a quantidade de arquivos.
        saida.writeUTF(
                String.valueOf(quantidade)
        );

        // Envia o nome de cada arquivo.
        for (File arquivo : arquivos) {

            if (arquivo.isFile()) {

                saida.writeUTF(
                        arquivo.getName()
                );
            }
        }

        saida.flush();
    }

    /**
     * Envia para o cliente um arquivo solicitado.
     *
     * O servidor verifica se o arquivo existe e,
     * caso exista, envia seu tamanho e seus bytes.
     */
    private static void enviarArquivo(
            DataInputStream entrada,
            DataOutputStream saida,
            File pastaArquivos) throws IOException {

        // Recebe o nome do arquivo solicitado.
        String nomeArquivo = entrada.readUTF();

        File arquivo = new File(
                pastaArquivos,
                nomeArquivo
        );

        // Verifica se o arquivo existe.
        if (!arquivo.exists() || !arquivo.isFile()) {

            saida.writeUTF("ARQUIVO_NAO_ENCONTRADO");
            saida.flush();

            return;
        }

        // Informa ao cliente que o arquivo existe.
        saida.writeUTF("ARQUIVO_ENCONTRADO");

        // Envia o tamanho do arquivo.
        saida.writeLong(
                arquivo.length()
        );

        try (FileInputStream arquivoEntrada =
                     new FileInputStream(arquivo)) {

            byte[] buffer = new byte[4096];

            int bytesLidos;

            // Envia os bytes do arquivo para o cliente.
            while ((bytesLidos =
                    arquivoEntrada.read(buffer)) != -1) {

                saida.write(
                        buffer,
                        0,
                        bytesLidos
                );
            }
        }

        saida.flush();

        System.out.println(
                "Arquivo enviado para o cliente: "
                        + nomeArquivo
        );
    }
}