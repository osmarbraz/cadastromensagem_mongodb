# Aplicação CRUD de Mensagens usando Java e MongoDB

## Contextualização

- Esta é uma versão do sistema desenvolvida para a IDE NetBeans.
- O projeto no NetBeans deve ser chamado **cadastromensagem_mongodb**.
- Utiliza o **Java 8**.
- Utiliza o **Apache Maven** para automatizar o processo de construção da aplicação.
- A aplicação é empacotada no formato **JAR (Java ARchive)**.
- Os dados das mensagens são armazenados no banco de dados MongoDB.
- A aplicação permite realizar as operações CRUD (*Create, Read, Update e Delete*), ou seja, inserir, consultar, atualizar e excluir mensagens.
- O programa utiliza Java para implementar as funcionalidades de cadastro e gerenciamento das mensagens.
- A pasta `src` contém os arquivos-fonte do projeto.
- A conexão com o MongoDB é realizada por meio do driver oficial para Java.

## Banco de dados

A aplicação utiliza o MongoDB, um banco de dados NoSQL orientado a documentos.

O banco de dados e a coleção são criados automaticamente pelo MongoDB quando a aplicação realiza a primeira gravação, caso ainda não existam.

- **Banco de dados:** `mensagensDB`
- **Coleção:** `mensagens`

Cada documento representa uma mensagem armazenada na coleção. Um documento contêm os campos: id, título, conteudo e autor.

Utilize o repositório [https://github.com/osmarbraz/mongodb_express_docker](https://github.com/osmarbraz/mongodb_express_docker) para iniciar o serviço do banco de dados.

### Configuração da conexão

A aplicação utiliza as seguintes configurações para conectar-se ao MongoDB:

- **Servidor:** `localhost`
- **Porta:** `27017`
- **Usuário:** `root`
- **Senha:** `root`
- **Banco de autenticação:** `admin`

A URI de conexão utilizada no projeto é:

```text
mongodb://root:root@localhost:27017/?authSource=admin
```

Essas configurações pressupõem que o MongoDB esteja em execução localmente, com o usuário `root` configurado no banco de autenticação `admin`.

## Operações CRUD

A aplicação implementa as seguintes operações:

- **Create (Inserir):** cadastra uma nova mensagem na coleção.
- **Read (Consultar):** consulta e apresenta as mensagens armazenadas.
- **Update (Atualizar):** modifica uma mensagem já cadastrada.
- **Delete (Excluir):** remove uma mensagem da coleção.

As operações são realizadas por meio dos recursos disponibilizados pelo driver oficial do MongoDB para Java.

## Tecnologias utilizadas

- **Java:** linguagem de programação utilizada no desenvolvimento.
- **MongoDB:** sistema gerenciador de banco de dados NoSQL.
- **MongoDB Java Driver:** biblioteca responsável pela comunicação entre a aplicação Java e o banco de dados.
- **Maven:** ferramenta utilizada para gerenciar dependências e realizar a compilação.
- **IDE NetBeans:** ambiente de desenvolvimento utilizado para editar, compilar e executar o projeto.

## Execução do projeto

1. Instale e inicie o MongoDB.
2. Configure o usuário e a senha de acordo com a URI utilizada no código.
3. Abra o projeto `cadastromensagem_mongodb` na IDE NetBeans.
4. Verifique se as dependências do Maven foram baixadas corretamente.
5. Execute a classe principal `Principal.java`.
6. Utilize o menu da aplicação para inserir, consultar, atualizar e excluir mensagens.

## Arquivos

- `pom.xml` — arquivo de configuração do Maven, responsável por definir as dependências do projeto.
- `src/main/java/Principal.java` — classe principal da aplicação, responsável pela interação com o usuário e pelas operações CRUD.

