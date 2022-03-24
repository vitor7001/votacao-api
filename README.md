# Desafio Sicredi

Repositório de uma API escrita em **Java** utilizando **Spring Boot** como framework e **H2** como banco de dados para permitir o CRUD de pautas e realizar o voto nas mesmas.

É necessário ter o RabbitMQ rodando para o uso de filas, recomendo seguir algum tutorial como este: https://medium.com/xp-inc/rabbitmq-com-docker-conhecendo-o-admin-cc81f3f6ac3b
Ou se já tiver algum configurado em sua máquina verifique as propriedades no application.properties para validar se estão de acordo com as suas configurações
# Baixando o projeto

Tenhas as seguintes ferramentas instaladas em sua máquina:

 - [ ] Java 8
 - [ ] Maven
 - [ ] Variáveis de ambientes das tecnologias acima devidamente configuradas

Após o download acesse a pasta do projeto:
 - [ ] cd votacao-api

Agora instale as dependências do projeto, execute o comando:

 - [ ] mvn install
 

# Executando o projeto
Estando na pasta do projeto e após baixar as dependências do mesmo execute o comando:

 - [ ] mvn spring-boot:run

Então a aplicação começara a ser inicializada, com seu banco de testes.

Após a aplicação ser inicializada você ter acesso as suas devidas rotas pela url: [swagger](http://localhost:8080)
que levará você para um link da aplicação que está rodando localmente e com todos os endpoints utilizados na resolução do desafio.
Há também uma coleção do Postman com os endpoints para facilitar o uso da api, baixe ela e execute as requisições.

# Executando os testes
Para a execução dos testes para a execução da aplicação com um  `ctrl + C`
e aceitando a finalização de arquivos em lotes.

Após isso você pode limpar a tela com um `cls` e executar o comando de testes:

 - [ ] mvn test

e esperar a execução dos mesmos.
