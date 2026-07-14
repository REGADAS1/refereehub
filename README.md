# RefereeHub

RefereeHub é uma aplicação pensada para ajudar árbitros de futebol a organizar e acompanhar a sua atividade ao longo de uma época desportiva.

O objetivo principal é substituir registos dispersos, como folhas de cálculo, notas soltas ou mensagens, por uma plataforma centralizada onde seja possível guardar jogos, nomeações, pagamentos, quilómetros, subsídios, relatórios e estatísticas pessoais.

Atualmente, o projeto encontra-se focado no desenvolvimento do backend, construído com Java, Spring Boot, PostgreSQL, Flyway, JPA/Hibernate, validações, testes automáticos e documentação Swagger/OpenAPI.

---

## Objetivo da aplicação

Durante uma época, um árbitro pode acumular muitos jogos em diferentes competições, escalões, divisões e funções. Além disso, precisa frequentemente de acompanhar pagamentos, prémios, quilómetros, subsídios noturnos, relatórios submetidos e jogos ainda pendentes.

O RefereeHub nasce para resolver esse problema.

A aplicação permite ao árbitro manter um histórico organizado da sua atividade, consultar rapidamente informação importante e acompanhar a evolução da época de forma simples e estruturada.

---

## Problema que resolve

Muitos árbitros acabam por controlar a sua época através de folhas de cálculo, mensagens, documentos separados ou memória pessoal. Com o tempo, torna-se difícil responder rapidamente a perguntas como:

- Quantos jogos já fiz esta época?
- Quanto recebi este mês?
- Que jogos ainda não foram pagos?
- Em que escalão arbitrei mais vezes?
- Quantos quilómetros fiz?
- Quanto tenho ainda por receber?
- Qual foi o meu histórico recente de nomeações?

O RefereeHub procura centralizar estas respostas numa única aplicação.

---

## Funcionalidades implementadas

### Gestão de jogos

A API permite:

- criar jogos;
- listar jogos;
- procurar um jogo por id;
- editar jogos;
- apagar jogos;
- filtrar jogos por estado;
- filtrar jogos por intervalo de datas.

Cada jogo contém informação como:

- data;
- hora;
- função desempenhada;
- escalão;
- divisão;
- equipa da casa;
- equipa visitante;
- local;
- estado do jogo.

Estados suportados:

```text
SCHEDULED
COMPLETED
CANCELLED
REPORT_SUBMITTED
```

### Gestão financeira

A API permite associar um registo financeiro a cada jogo.

Cada pagamento pode incluir:

- prémio do jogo;
- estado de pagamento;
- data de pagamento;
- quilómetros;
- valor por quilómetro;
- subsídio noturno;
- observações.

A API calcula automaticamente:

```text
mileageAmount = kilometers × kmRate
totalAmount = feeAmount + mileageAmount + nightSubsidyAmount
```

O subsídio noturno é aplicado através de um campo booleano:

```text
nightSubsidyApplied = true  -> nightSubsidyAmount = 13.00
nightSubsidyApplied = false -> nightSubsidyAmount = 0.00
```

### Pagamentos pendentes

A API permite consultar pagamentos ainda não recebidos:

```http
GET /api/payments/pending
```

### Resumo financeiro nos jogos

A listagem de jogos já devolve também um resumo financeiro associado ao jogo.

Exemplo:

```json
{
  "id": 6,
  "date": "2026-07-06",
  "time": "16:00:00",
  "role": "REFEREE",
  "ageGroup": "Seniores",
  "division": "Distrital",
  "homeTeam": "Águias Alvelos",
  "awayTeam": "SC Cabreiros",
  "venue": "Campo Novo",
  "status": "SCHEDULED",
  "paymentSummary": {
    "id": 1,
    "feeAmount": 42.50,
    "mileageAmount": 10.08,
    "nightSubsidyApplied": true,
    "nightSubsidyAmount": 13.00,
    "totalAmount": 65.58,
    "paid": false
  }
}
```

### Dashboard

A API já disponibiliza endpoints para estatísticas e resumo da época.

Funcionalidades implementadas:

- resumo global;
- resumo filtrado por datas;
- jogos por função;
- jogos por escalão;
- ganhos por mês.

Exemplos:

```http
GET /api/dashboard/summary
GET /api/dashboard/summary?startDate=2026-07-01&endDate=2026-07-31
GET /api/dashboard/matches-by-role
GET /api/dashboard/matches-by-age-group
GET /api/dashboard/earnings-by-month
```

---

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Hibernate ORM
- Jakarta Validation
- Maven

### Base de dados

- PostgreSQL
- Flyway migrations
- Docker Compose

### Documentação da API

- Swagger UI
- OpenAPI 3
- springdoc-openapi

### Testes

- JUnit 5
- Mockito
- Spring Boot Test

### Ferramentas de desenvolvimento

- Visual Studio Code
- Insomnia
- Docker Desktop
- Git
- GitHub

---

## Arquitetura atual

A arquitetura atual segue uma estrutura típica de backend REST:

```text
Cliente externo, Swagger, Insomnia ou futuro frontend
        ↓
Controllers REST
        ↓
Services
        ↓
Repositories
        ↓
PostgreSQL
```

No futuro, a arquitetura será:

```text
Frontend
        ↓
Spring Boot REST API
        ↓
PostgreSQL
```

O frontend ainda não foi desenvolvido. Atualmente, a API é testada através de Swagger UI e Insomnia.

---

## Estrutura do backend

O projeto está organizado por camadas:

```text
controller
domain
dto
exception
repository
service
config
```

### controller

Contém os endpoints REST.

Exemplos:

- `MatchController`
- `PaymentController`
- `DashboardController`

### service

Contém a lógica de negócio.

Exemplos:

- validação de regras de pagamento;
- cálculo de valores;
- filtragem de dados;
- estatísticas do dashboard.

### repository

Contém o acesso à base de dados através de Spring Data JPA.

Exemplos:

- `MatchRepository`
- `PaymentRepository`

### domain

Contém as entidades JPA.

Exemplos:

- `Match`
- `Payment`

### dto

Contém os objetos usados para entrada e saída da API.

Exemplos:

- `CreateMatchRequest`
- `UpdateMatchRequest`
- `MatchResponse`
- `CreatePaymentRequest`
- `PaymentResponse`
- `DashboardSummaryResponse`

### exception

Contém exceções personalizadas e tratamento global de erros.

---

## Modelo da base de dados

### Tabela `matches`

Guarda os dados principais dos jogos.

Campos principais:

```text
id
match_date
match_time
referee_role
age_group
division
home_team
away_team
venue
status
home_goals
away_goals
```

### Tabela `payments`

Guarda os dados financeiros associados a cada jogo.

Campos principais:

```text
id
match_id
fee_amount
paid
paid_at
kilometers
km_rate
night_subsidy_applied
night_subsidy_amount
notes
```

A relação entre jogo e pagamento é:

```text
Match 1 ─── 1 Payment
```

Isto significa que cada jogo pode ter, no máximo, um registo financeiro.

---

## Flyway migrations

A estrutura da base de dados é controlada através de migrations Flyway.

Migrations atuais:

```text
V1__create_matches_table.sql
V2__create_payments_table.sql
```

O Flyway garante que a base de dados evolui de forma controlada e que as alterações ficam registadas.

---

## Regras de negócio

### Regras dos jogos

Um jogo deve ter obrigatoriamente:

- data;
- função;
- equipa da casa;
- equipa visitante;
- estado.

Se forem usados filtros de data, `startDate` e `endDate` devem ser enviados em conjunto.

Exemplo válido:

```http
GET /api/matches?startDate=2026-07-01&endDate=2026-07-31
```

Exemplo inválido:

```http
GET /api/matches?startDate=2026-07-01
```

### Regras dos pagamentos

Um pagamento deve ter um prémio maior do que zero.

Regras relacionadas com pagamento:

```text
paid = true  -> paidAt é obrigatório
paid = false -> paidAt deve ser null
```

Regras relacionadas com quilómetros:

```text
kilometers e kmRate devem ser enviados em conjunto
```

---

## Endpoints principais

## Home

```http
GET /
```

Verifica se a API está a correr.

---

## Matches

### Listar jogos

```http
GET /api/matches
```

Filtros opcionais:

```text
status
startDate
endDate
```

Exemplos:

```http
GET /api/matches
GET /api/matches?status=SCHEDULED
GET /api/matches?startDate=2026-07-01&endDate=2026-07-31
GET /api/matches?status=SCHEDULED&startDate=2026-07-01&endDate=2026-07-31
```

### Obter jogo por id

```http
GET /api/matches/{id}
```

### Criar jogo

```http
POST /api/matches
```

Exemplo:

```json
{
  "date": "2026-07-06",
  "time": "16:00:00",
  "role": "REFEREE",
  "ageGroup": "Seniores",
  "division": "Distrital",
  "homeTeam": "Águias Alvelos",
  "awayTeam": "SC Cabreiros",
  "venue": "Campo Novo",
  "status": "SCHEDULED"
}
```

### Atualizar jogo

```http
PUT /api/matches/{id}
```

### Apagar jogo

```http
DELETE /api/matches/{id}
```

Resposta esperada:

```http
204 No Content
```

---

## Payments

### Criar pagamento para um jogo

```http
POST /api/matches/{matchId}/payment
```

Exemplo de pagamento por receber:

```json
{
  "feeAmount": 42.50,
  "paid": false,
  "paidAt": null,
  "kilometers": 28,
  "kmRate": 0.36,
  "nightSubsidyApplied": true,
  "notes": "Jogo com subsídio noturno"
}
```

Exemplo de pagamento já recebido:

```json
{
  "feeAmount": 42.50,
  "paid": true,
  "paidAt": "2026-07-13",
  "kilometers": 28,
  "kmRate": 0.36,
  "nightSubsidyApplied": true,
  "notes": "Pagamento recebido por transferência bancária"
}
```

### Obter pagamento de um jogo

```http
GET /api/matches/{matchId}/payment
```

### Atualizar pagamento

```http
PUT /api/payments/{id}
```

### Listar pagamentos pendentes

```http
GET /api/payments/pending
```

---

## Dashboard

### Resumo geral

```http
GET /api/dashboard/summary
```

Com filtro por datas:

```http
GET /api/dashboard/summary?startDate=2026-07-01&endDate=2026-07-31
```

Exemplo de resposta:

```json
{
  "totalMatches": 2,
  "scheduledMatches": 2,
  "completedMatches": 0,
  "totalEarned": 131.16,
  "totalReceived": 65.58,
  "totalPending": 65.58,
  "pendingPayments": 1,
  "totalKilometers": 56.00
}
```

### Jogos por função

```http
GET /api/dashboard/matches-by-role
```

Com filtro por datas:

```http
GET /api/dashboard/matches-by-role?startDate=2026-07-01&endDate=2026-07-31
```

### Jogos por escalão

```http
GET /api/dashboard/matches-by-age-group
```

Com filtro por datas:

```http
GET /api/dashboard/matches-by-age-group?startDate=2026-07-01&endDate=2026-07-31
```

### Ganhos por mês

```http
GET /api/dashboard/earnings-by-month
```

Com filtro por datas:

```http
GET /api/dashboard/earnings-by-month?startDate=2026-01-01&endDate=2026-02-28
```

Exemplo de resposta:

```json
[
  {
    "month": "2026-01",
    "totalEarned": 65.58,
    "totalReceived": 65.58,
    "totalPending": 0
  },
  {
    "month": "2026-02",
    "totalEarned": 57.20,
    "totalReceived": 0,
    "totalPending": 57.20
  }
]
```

---

## Tratamento de erros

A API devolve respostas de erro estruturadas.

Exemplo:

```json
{
  "timestamp": "2026-07-14T10:13:38.4303511",
  "status": 400,
  "error": "Bad Request",
  "message": "endDate cannot be before startDate.",
  "path": "/api/matches"
}
```

Erros comuns:

```text
400 Bad Request
Dados inválidos, intervalo de datas inválido, pagamento inválido ou JSON mal formado.

404 Not Found
Jogo ou pagamento não encontrado.

409 Conflict
Já existe pagamento para o jogo.

500 Internal Server Error
Erro inesperado no servidor.
```

---

## Swagger/OpenAPI

A API está documentada com Swagger/OpenAPI através de `springdoc-openapi`.

Depois de iniciar a aplicação, a documentação visual fica disponível em:

```text
http://localhost:8080/swagger-ui.html
```

O documento OpenAPI em JSON fica disponível em:

```text
http://localhost:8080/v3/api-docs
```

O Swagger permite consultar e testar todos os endpoints diretamente no browser.

---

## Como executar o projeto

### Requisitos

- Java 21
- Docker Desktop
- Maven Wrapper incluído no projeto
- Git

### Iniciar a aplicação

Garantir primeiro que o Docker Desktop está aberto.

Depois, no terminal:

```powershell
.\mvnw spring-boot:run
```

A aplicação fica disponível em:

```text
http://localhost:8080
```

### Executar testes

```powershell
.\mvnw test
```

ou:

```powershell
.\mvnw clean test
```

---

## URLs úteis

```text
API root:
http://localhost:8080/

Swagger UI:
http://localhost:8080/swagger-ui.html

OpenAPI JSON:
http://localhost:8080/v3/api-docs

Matches:
http://localhost:8080/api/matches

Dashboard summary:
http://localhost:8080/api/dashboard/summary
```

---

## Testes automáticos

O projeto inclui testes unitários para validar regras importantes da aplicação.

Exemplos de testes implementados:

- criação de pagamento;
- cálculo de `mileageAmount`;
- cálculo de `totalAmount`;
- validação de `paidAt`;
- validação de `kilometers` e `kmRate`;
- resumo financeiro do dashboard;
- agrupamento por função;
- agrupamento por escalão;
- ganhos por mês.

Tecnologias utilizadas nos testes:

- JUnit 5;
- Mockito.

---

## Estado atual do projeto

Implementado:

- API REST com Spring Boot;
- base de dados PostgreSQL;
- migrations com Flyway;
- CRUD de jogos;
- filtros de jogos;
- gestão de pagamentos;
- cálculo de valores financeiros;
- pagamentos pendentes;
- resumo financeiro nos jogos;
- dashboard global;
- dashboard filtrado por datas;
- estatísticas por função;
- estatísticas por escalão;
- ganhos por mês;
- validações de negócio;
- tratamento global de erros;
- documentação Swagger/OpenAPI;
- testes automáticos básicos.

Ainda não implementado:

- frontend;
- autenticação;
- relatórios de jogo;
- eventos do jogo, como golos, cartões e incidentes;
- exportação CSV ou Excel;
- exportação PDF;
- AI Helper para apoio à redação de relatórios;
- deploy.

---

## Próximos passos

Funcionalidades futuras previstas:

- criação do frontend;
- dashboard visual com gráficos;
- módulo de relatórios de jogo;
- registo de eventos do jogo;
- exportação de dados;
- geração de relatórios em PDF;
- AI Helper para transformar notas soltas em texto formal;
- autenticação e contas de utilizador;
- mais testes automáticos;
- deploy com Docker.

---

## Resumo

RefereeHub é uma aplicação para árbitros gerirem a sua época de forma mais organizada.

A aplicação centraliza jogos, pagamentos, quilómetros, subsídios, relatórios e estatísticas, ajudando o árbitro a ter maior controlo sobre a sua atividade e a consultar rapidamente informação importante ao longo da época.

Atualmente, o projeto já possui um backend funcional, documentado e testado, pronto para servir de base ao futuro frontend.

## Project learning goals

This project was built to practise and demonstrate:

- Java backend development
- Spring Boot architecture
- REST API design
- Layered architecture
- DTO usage
- Database modelling
- JPA and Hibernate
- Flyway migrations
- PostgreSQL
- Docker-based development
- Validation and error handling
- Swagger/OpenAPI documentation
- Unit testing with JUnit and Mockito
- Git and GitHub workflow