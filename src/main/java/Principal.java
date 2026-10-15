
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Aplicação de console que implementa as operações CRUD de mensagens
 * armazenadas em uma base de dados MongoDB.
 *
 * O sistema permite cadastrar, listar, consultar, atualizar e excluir mensagens
 * da coleção {@code mensagens}, pertencente à base {@code mensagensDB}.
 *
 * A conexão utiliza o usuário {@code root}, a senha {@code root} e o banco
 * {@code admin} como origem de autenticação. 
 */
public class Principal {

    /**
     * Leitor utilizado para receber dados digitados pelo usuário no console.
     *
     * O mesmo objeto é reutilizado pelos métodos da aplicação para evitar a
     * criação de múltiplos leitores sobre {@link System#in}.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Ponto de entrada da aplicação.
     *
     * Estabelece a conexão com o MongoDB, seleciona a base e a coleção,
     * apresenta o menu principal e direciona a execução para a operação
     * escolhida pelo usuário. O cliente MongoDB é fechado automaticamente ao
     * final do bloco {@code try-with-resources}.
     *
     * @param args argumentos recebidos pela linha de comando; não são
     * utilizados nesta aplicação
     */
    public static void main(String[] args) {

        // Variáveis: URI com usuário, senha e banco de autenticação.
        String uri = "mongodb://root:root@localhost:27017/?authSource=admin";

        // Processamento: estabelece a conexão com o MongoDB.
        try ( MongoClient clienteMongoDB = MongoClients.create(uri)) {

            // Variáveis: seleciona o banco e a coleção.
            MongoDatabase banco = clienteMongoDB.getDatabase("mensagensDB");
            MongoCollection<Document> mensagens = banco.getCollection("mensagens");

            //Variável de controle do menu
            int opcao;
            do {
                // Saída: apresenta o menu do sistema.
                System.out.println("=== MENU CADASTRO DE MENSAGENS ===");
                System.out.println("1 - Cadastrar mensagem");
                System.out.println("2 - Listar mensagens");
                System.out.println("3 - Consultar mensagem por ID");
                System.out.println("4 - Atualizar mensagem");
                System.out.println("5 - Excluir mensagem");
                System.out.println("9 - Sair");

                // Entrada: recebe a opção escolhida pelo usuário.
                // A leitura por nextLine evita deixar a quebra de linha
                // pendente para os métodos que também leem texto.
                System.out.print("Escolha uma opcao: ");
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consome a quebra de linha pendente.

                try {
                    switch (opcao) {
                        case 1:
                            cadastrar(mensagens);
                            break;
                        case 2:
                            listar(mensagens);
                            break;
                        case 3:
                            consultar(mensagens);
                            break;
                        case 4:
                            atualizar(mensagens);
                            break;
                        case 5:
                            excluir(mensagens);
                            break;
                        case 9:
                            System.out.println("Programa encerrado.");
                            break;
                        default:
                            System.out.println("Opcao invalida.");
                            break;
                    }
                } catch (MongoException e) {
                    System.out.println("Erro no MongoDB: " + e.getMessage());
                }
            } while (opcao != 9);

        } catch (MongoException e) {
            // Saída: informa falhas na conexão ou autenticação.
            System.out.println("Nao foi possível conectar ao MongoDB.");
            System.out.println("Verifique o servidor, o usuario e a senha.");
            System.out.println(e.getMessage());

        } finally {
            scanner.close();
        }
    }

    /**
     * Cadastra uma nova mensagem na coleção do MongoDB.
     *
     * Solicita título, conteúdo e autor, verifica se os campos estão
     * preenchidos, cria um documento BSON e o insere na coleção. O MongoDB gera
     * automaticamente o identificador {@code _id} quando ele não é informado.
     *
     * @param mensagens coleção MongoDB na qual a mensagem será inserida
     */
    private static void cadastrar(MongoCollection<Document> mensagens) {

        System.out.println("\n--- CADASTRAR MENSAGEM ---");

        // Entrada: recebe os dados da mensagem.
        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Conteudo: ");
        String conteudo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        // Processamento: valida os campos obrigatórios.
        if (titulo.isEmpty() || conteudo.isEmpty() || autor.isEmpty()) {
            System.out.println("Todos os campos sao obrigatorios.");
            return;
        }

        // Processamento: monta o documento BSON.
        Document mensagem = new Document()
                .append("titulo", titulo)
                .append("conteudo", conteudo)
                .append("autor", autor);

        // Processamento: insere o documento na coleção.
        InsertOneResult resultado = mensagens.insertOne(mensagem);

        // Saída: apresenta o identificador gerado.
        System.out.println("Mensagem cadastrada com sucesso!");
        System.out.println("ID: " + resultado.getInsertedId());
    }

    /**
     * Lista todas as mensagens cadastradas, ordenadas pelo título.
     *
     * Se a coleção estiver vazia, informa ao usuário que não há mensagens
     * cadastradas.
     *
     * @param mensagens coleção MongoDB consultada
     */
    private static void listar(MongoCollection<Document> mensagens) {

        System.out.println("\n--- LISTA DE MENSAGENS ---");

        // Variáveis: controla se existem mensagens.
        boolean encontrou = false;

        // Processamento: percorre os documentos em ordem de título.
        for (Document mensagem : mensagens.find().sort(ascending("titulo"))) {
            // Saída: apresenta cada mensagem.
            exibirMensagem(mensagem);
            encontrou = true;
        }

        if (!encontrou) {
            System.out.println("Nenhuma mensagem cadastrada.");
        }
    }

    /**
     * Consulta e exibe uma mensagem pelo identificador MongoDB.
     *
     * O identificador informado deve possuir o formato válido de um
     * {@link ObjectId}. Caso seja inválido ou não corresponda a um documento, a
     * operação é encerrada com uma mensagem apropriada.
     *
     * @param mensagens coleção MongoDB na qual a busca será realizada
     */
    private static void consultar(MongoCollection<Document> mensagens) {

        System.out.println("\n--- CONSULTAR MENSAGEM ---");

        // Entrada: recebe e valida o ID.
        System.out.print("Informe o ID da mensagem: ");
        String texto = scanner.nextLine().trim();

        // Verifica se o texto do id é válido
        if (!ObjectId.isValid(texto)) {
            System.out.println("ID invalido.");

        } else {
            ObjectId id = new ObjectId(texto);

            // Processamento: procura o documento pelo ID.
            Document mensagem = mensagens.find(eq("_id", id)).first();

            // Saída: apresenta o resultado da consulta.
            if (mensagem == null) {
                System.out.println("Mensagem nao encontrada.");
            } else {
                exibirMensagem(mensagem);
            }
        }
    }

    /**
     * Atualiza o título, o conteúdo e o autor de uma mensagem existente.
     *
     * Primeiro verifica se o documento existe. Em seguida, solicita os novos
     * valores, valida os campos e atualiza os três atributos do documento
     * identificado pelo {@code _id} informado.
     *
     * @param mensagens coleção MongoDB na qual a mensagem será atualizada
     */
    private static void atualizar(MongoCollection<Document> mensagens) {

        System.out.println("\n--- ATUALIZAR MENSAGEM ---");

        // Entrada: recebe o ID da mensagem.
        System.out.print("Informe o ID da mensagem: ");
        String texto = scanner.nextLine().trim();

        // Verifica se o texto do id é válido
        if (!ObjectId.isValid(texto)) {
            System.out.println("ID invalido.");
        } else {
            ObjectId id = new ObjectId(texto);

            // Processamento: verifica se a mensagem existe.
            Document existente = mensagens.find(eq("_id", id)).first();

            if (existente == null) {
                System.out.println("Mensagem não encontrada.");
                return;
            }

            // Saída: apresenta os dados atuais.
            System.out.println("Mensagem atual:");
            exibirMensagem(existente);

            // Entrada: recebe os novos valores.
            System.out.println("Digite os novos dados:");
            System.out.print("Novo titulo: ");
            String titulo = scanner.nextLine();
            System.out.print("Novo conteudo: ");
            String conteudo = scanner.nextLine();
            System.out.print("Novo autor: ");
            String autor = scanner.nextLine();

            // Processamento: valida os campos.
            if (titulo.isEmpty() || conteudo.isEmpty() || autor.isEmpty()) {
                System.out.println("Todos os campos sao obrigatorios.");
                return;
            }

            // Processamento: atualiza os campos no banco.
            UpdateResult resultado = mensagens.updateOne(
                    eq("_id", id),
                    combine(set("titulo", titulo), set("conteudo", conteudo), set("autor", autor)
                    )
            );

            // Saída: informa o resultado da operação.
            if (resultado.getModifiedCount() > 0) {
                System.out.println("Mensagem atualizada com sucesso!");
            } else {
                System.out.println("Nenhuma alteracao foi realizada.");
            }
        }
    }

    /**
     * Exclui uma mensagem após solicitar confirmação ao usuário.
     *
     * Busca o documento pelo identificador, apresenta seus dados e solicita
     * confirmação. Somente a resposta {@code S}, sem distinção entre maiúsculas
     * e minúsculas, confirma a exclusão.
     *
     * @param mensagens coleção MongoDB da qual a mensagem poderá ser excluída
     */
    private static void excluir(MongoCollection<Document> mensagens) {

        System.out.println("\n--- EXCLUIR MENSAGEM ---");

        // Entrada: recebe o ID da mensagem.
        System.out.print("Informe o ID da mensagem: ");
        String texto = scanner.nextLine().trim();
        // Verifica se o texto do id é válido
        if (!ObjectId.isValid(texto)) {
            System.out.println("ID invalido.");
        } else {
            ObjectId id = new ObjectId(texto);

            // Processamento: verifica se a mensagem existe.
            Document mensagem = mensagens.find(eq("_id", id)).first();

            if (mensagem == null) {
                System.out.println("Mensagem nao encontrada.");
                return;
            }

            // Saída: mostra os dados antes da exclusão.
            exibirMensagem(mensagem);

            // Entrada: solicita confirmação.
            System.out.print("Confirma a exclusao? (S/N): ");
            String confirmacao = scanner.nextLine().trim();

            if (!confirmacao.equalsIgnoreCase("S")) {
                System.out.println("Exclusão cancelada.");
                return;
            }

            // Processamento: remove o documento do banco.
            DeleteResult resultado = mensagens.deleteOne(eq("_id", id));

            // Saída: informa o resultado da exclusão.
            if (resultado.getDeletedCount() > 0) {
                System.out.println("Mensagem excluida com sucesso!");
            } else {
                System.out.println("Nenhuma mensagem foi excluida.");
            }
        }
    }

    /**
     * Exibe no console os campos de um documento de mensagem.
     *
     * @param mensagem documento MongoDB que contém os dados da mensagem;
     * espera-se que possua os campos {@code _id}, {@code titulo},
     * {@code conteudo} e {@code autor}
     */
    private static void exibirMensagem(Document mensagem) {
        System.out.println("------------------------------");
        System.out.println("ID: " + mensagem.getObjectId("_id"));
        System.out.println("Titulo: " + mensagem.getString("titulo"));
        System.out.println("Conteudo: " + mensagem.getString("conteudo"));
        System.out.println("Autor: " + mensagem.getString("autor"));
    }

}
