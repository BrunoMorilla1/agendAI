```
## Contexto do Projeto

O agendAI foi criado como parte da minha jornada de aprendizado em desenvolvimento de software.
O principal objetivo é aprimorar meus conhecimentos em Java, Spring Boot e no desenvolvimento de APIs RESTful.
Ao longo do processo, estou explorando como construir aplicações que sejam não apenas funcionais, mas também úteis.
Este projeto me permite aplicar conceitos estudados e evoluir minha lógica de programação. 

## Descrição

Este projeto é uma API RESTful de gerenciamento de tarefas (Todo List) desenvolvida com Java e Spring Boot.
Ela permite realizar operações CRUD (criar, ler, atualizar e excluir) de tarefas, facilitando o controle e organização de atividades do dia a dia.

## Funcionalidades

Criar uma nova tarefa
Listar todas as tarefas
Buscar uma tarefa por ID
Atualizar uma tarefa existente
Marcar uma tarefa como concluída
Reabrir uma tarefa
Remover uma tarefa

## Tecnologias Utilizadas

Java 17
Spring Boot 2.7.18
Lombok
SpringDoc OpenAPI – documentação automática da API
JUnit 5 – testes unitários
Banco de Dados: simulação em memória utilizando ConcurrentHashMap

## Melhorias em Andamento

Atualmente, estou aprimorando os tratamentos de erros da aplicação

## Estrutura do Projeto
bash
Copiar
Editar
src
├── main
│   ├── java
│   │   └── br/com/agendai/agendai
│   │       ├── AgendAiApplication.java
│   │       ├── config
│   │       │   └── ConfiguracaoOpenApi.java
│   │       ├── controller
│   │       │   └── TarefaControle.java
│   │       ├── exception
│   │       │   ├── RespostaErro.java
│   │       │   ├── ManipuladorExcecaoGlobal.java
│   │       │   └── TarefaNaoEncontradaException.java
│   │       ├── model
│   │       │   ├── AtualizarTarefa.java
│   │       │   ├── PrioridadeTarefa.java
│   │       │   ├── RequisicaoCriacaoTarefa.java
│   │       │   ├── StatusTarefa.java
│   │       │   └── Tarefa.java
│   │       ├── service
│   │       │   ├── ObterEstatisticas.java
│   │       │   └── ServicoTarefa.java
│   └── resources
│       └── application.properties
├── test
│   └── java/br/com/agendai/agendai
│       ├── TarefaControllerTest.java
│       └── ServicoTarefaTeste.java
└── pom.xml
