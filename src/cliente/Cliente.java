package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    // Endereço do servidor.
    private static final String ENDERECO_SERVIDOR = "localhost";

    // Porta utilizada pelo servidor.
    private static final int PORTA = 5000;

    public static void main(String[] args) {

        System.out.println("=== CLIENTE SiCA ===");

        System.out.println(
                "Conectando ao servidor "
                        + ENDERECO_SERVIDOR
                        + ":"
                        + PORTA
                        + "..."
        );

        try (Socket socket = new Socket(
                ENDERECO_SERVIDOR,
                PORTA
        )) {

            System.out.println(
                    "Conectado com sucesso!"
            );

            DataInputStream entrada =
                    new DataInputStream(
                            socket.getInputStream()
                    );

            DataOutputStream saida =
                    new DataOutputStream(
                            socket.getOutputStream()
                    );

            Scanner scanner =
                    new Scanner(System.in);

            int opcao;

            do {

                System.out.println();
                System.out.println("--- MENU SiCA ---");
                System.out.println("1 - Enviar arquivo");
                System.out.println("2 - Listar arquivos do servidor");
                System.out.println("3 - Baixar arquivo");
                System.out.println("4 - Sair");
                System.out.print("Escolha: ");

                opcao = scanner.nextInt();

                switch (opcao) {

                    case 1:

                        scanner.nextLine();

                        System.out.print(
                                "Digite o caminho do arquivo: "
                        );

                        String caminhoArquivo =
                                scanner.nextLine();

                        enviarArquivo(
                                caminhoArquivo,
                                saida,
                                entrada
                        );

                        break;

                    case 2:

                        listarArquivos(
                                saida,
                                entrada
                        );

                        break;

                    case 3:

                        scanner.nextLine();

                        System.out.print(
                                "Digite o nome do arquivo: "
                        );

                        String nomeArquivo =
                                scanner.nextLine();

                        baixarArquivo(
                                nomeArquivo,
                                saida,
                                entrada
                        );

                        break;

                    case 4:

                        // Informa ao servidor que o cliente
                        // deseja encerrar a conexão.
                        saida.writeUTF("SAIR");
                        saida.flush();

                        System.out.println(
                                "Encerrando cliente..."
                        );

                        break;

                    default:

                        System.out.println(
                                "Opção inválida."
                        );
                }

            } while (opcao != 4);

            scanner.close();
            entrada.close();
            saida.close();

        } catch (IOException e) {

            System.out.println(
                    "Erro na conexão: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Envia um arquivo do computador do cliente
     * para o servidor.
     */
    private static void enviarArquivo(
            String caminhoArquivo,
            DataOutputStream saida,
            DataInputStream entrada) {

        File arquivo =
                new File(caminhoArquivo);

        // Verifica se o arquivo informado existe.
        if (!arquivo.exists() || !arquivo.isFile()) {

            System.out.println(
                    "Arquivo não encontrado."
            );

            return;
        }

        try {

            // Informa ao servidor que será enviado um arquivo.
            saida.writeUTF("ENVIAR");

            // Envia o nome do arquivo.
            saida.writeUTF(
                    arquivo.getName()
            );

            // Envia o tamanho do arquivo.
            saida.writeLong(
                    arquivo.length()
            );

            try (FileInputStream arquivoEntrada =
                         new FileInputStream(arquivo)) {

                byte[] buffer = new byte[4096];

                int bytesLidos;

                // Lê o arquivo em blocos e envia ao servidor.
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

            // Aguarda a confirmação do servidor.
            String resposta =
                    entrada.readUTF();

            if (resposta.equals(
                    "ARQUIVO_RECEBIDO")) {

                System.out.println(
                        "Arquivo enviado com sucesso!"
                );

            } else {

                System.out.println(
                        "O servidor não confirmou o envio."
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Erro ao enviar arquivo: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Solicita ao servidor a lista de arquivos disponíveis.
     */
    private static void listarArquivos(
            DataOutputStream saida,
            DataInputStream entrada) {

        try {

            // Envia o comando para o servidor.
            saida.writeUTF("LISTAR");
            saida.flush();

            // Recebe a resposta.
            String resposta =
                    entrada.readUTF();

            if (resposta.equals(
                    "NENHUM_ARQUIVO")) {

                System.out.println(
                        "Nenhum arquivo disponível no servidor."
                );

                return;
            }

            // Converte a quantidade recebida para inteiro.
            int quantidade =
                    Integer.parseInt(resposta);

            System.out.println();
            System.out.println(
                    "Arquivos disponíveis:"
            );

            // Recebe e mostra os nomes dos arquivos.
            for (int i = 0; i < quantidade; i++) {

                String nomeArquivo =
                        entrada.readUTF();

                System.out.println(
                        (i + 1)
                                + " - "
                                + nomeArquivo
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Erro ao listar arquivos: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Solicita um arquivo ao servidor e salva
     * o arquivo recebido no computador do cliente.
     */
    private static void baixarArquivo(
            String nomeArquivo,
            DataOutputStream saida,
            DataInputStream entrada) {

        try {

            // Informa ao servidor que deseja baixar um arquivo.
            saida.writeUTF("BAIXAR");

            // Envia o nome do arquivo solicitado.
            saida.writeUTF(nomeArquivo);

            saida.flush();

            // Recebe a resposta do servidor.
            String resposta =
                    entrada.readUTF();

            if (resposta.equals(
                    "ARQUIVO_NAO_ENCONTRADO")) {

                System.out.println(
                        "Arquivo não encontrado no servidor."
                );

                return;
            }

            // Recebe o tamanho do arquivo.
            long tamanhoArquivo =
                    entrada.readLong();

            // O arquivo será salvo na pasta
            // downloads do cliente.
            File pastaDownloads =
                    new File("downloads");

            if (!pastaDownloads.exists()) {
                pastaDownloads.mkdir();
            }

            File arquivo =
                    new File(
                            pastaDownloads,
                            nomeArquivo
                    );

            try (FileOutputStream arquivoSaida =
                         new FileOutputStream(arquivo)) {

                byte[] buffer = new byte[4096];

                long totalRecebido = 0;

                // Recebe os bytes do arquivo.
                while (totalRecebido <
                        tamanhoArquivo) {

                    int bytesParaLer =
                            (int) Math.min(
                                    buffer.length,
                                    tamanhoArquivo
                                            - totalRecebido
                            );

                    int bytesLidos =
                            entrada.read(
                                    buffer,
                                    0,
                                    bytesParaLer
                            );

                    if (bytesLidos == -1) {

                        throw new IOException(
                                "Conexão encerrada durante o download."
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
                    "Arquivo baixado com sucesso!"
            );

            System.out.println(
                    "Salvo em: "
                            + arquivo.getAbsolutePath()
            );

        } catch (IOException e) {

            System.out.println(
                    "Erro ao baixar arquivo: "
                            + e.getMessage()
            );
        }
    }
}