<div align="center">

# Banco Digital

### API backend em arquitetura de microsserviços com foco em clientes, contas e resiliência entre serviços

<p>
  <img alt="Java" src="https://img.shields.io/badge/Java-21-red?style=for-the-badge" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.5.9-6DB33F?style=for-the-badge" />
  <img alt="Spring Cloud" src="https://img.shields.io/badge/Spring_Cloud-OpenFeign%20%2B%20Resilience4j-0A66C2?style=for-the-badge" />
  <img alt="H2" src="https://img.shields.io/badge/Database-H2-blue?style=for-the-badge" />
  <img alt="Docker" src="https://img.shields.io/badge/Container-Docker-2496ED?style=for-the-badge" />
  <img alt="Swagger" src="https://img.shields.io/badge/API-OpenAPI%20%2F%20Swagger-85EA2D?style=for-the-badge" />
</p>

</div>

---

## Sobre o projeto

O **Banco Digital** é um projeto backend desenvolvido em **Java 21 + Spring Boot**, estruturado em **dois microsserviços** que simulam operações centrais de uma plataforma bancária:

- **serviço de clientes**
- **serviço de contas**

A proposta do projeto é demonstrar domínio em **arquitetura de APIs REST**, **comunicação entre serviços**, **validações de negócio**, **tratamento global de erros**, **documentação com Swagger** e **resiliência com OpenFeign + Resilience4j**.

Mais do que um CRUD, este repositório mostra uma base sólida para evoluir um sistema financeiro com organização por camadas, separação de responsabilidades e preocupação com confiabilidade.

---

## O que este projeto demonstra

- Modelagem de domínio para **clientes** e **contas bancárias**
- Arquitetura em **microsserviços desacoplados**
- Comunicação entre serviços via **OpenFeign**
- Resiliência com **Retry**, **Circuit Breaker** e **Fallback Factory**
- Boas práticas de API com **DTOs**, **service layer** e **repository layer**
- Persistência com **Spring Data JPA**
- Banco em memória **H2** para ambiente de desenvolvimento/teste
- **Swagger/OpenAPI** para documentação e exploração dos endpoints
- **Docker Compose** para subir o ambiente completo com facilidade
- **Logs**, **tratamento centralizado de exceções** e validações de entrada
- **Paginação**, filtro por status e regras de negócio no backend

---

## Arquitetura da solução

```text
banco-digital/
├── cliente/          # Microsserviço responsável pelo cadastro e gestão de clientes
├── conta-transf/     # Microsserviço responsável pela gestão de contas
└── docker-compose.yaml
```

### 1) Microsserviço de clientes
Responsável por cadastro, consulta, atualização, listagem e desativação de clientes.

#### Principais responsabilidades
- Criar cliente
- Buscar cliente por ID
- Listar clientes com paginação
- Filtrar clientes por status ativo/inativo
- Atualizar cliente
- Desativar cliente
- Remover cliente
- Garantir unicidade de **CPF** e **e-mail**

#### Diferenciais técnicos
- Validação com `jakarta.validation`
- DTOs para entrada e saída
- Handler global de exceções
- Retorno consistente para erros de negócio e validação
- Base inicial com dados via `import.sql`

---

### 2) Microsserviço de contas
Responsável pela abertura e manutenção de contas bancárias.

#### Principais responsabilidades
- Criar conta vinculada a um cliente existente
- Buscar conta por ID
- Listar contas com paginação
- Atualizar parcialmente tipo/status da conta
- Excluir conta

#### Regras de negócio importantes
- A conta só pode ser criada para um **cliente existente**
- A conta só pode ser criada para um **cliente ativo**
- O número da conta é gerado automaticamente
- A conta nasce com saldo inicial padronizado
- O status da conta é controlado por enum

#### Diferenciais técnicos
- Validação do cliente via **integração com o microsserviço cliente**
- **OpenFeign** para comunicação HTTP entre serviços
- **Retry** para novas tentativas automáticas
- **Circuit Breaker** com Resilience4j
- **Fallback Factory** para tratar indisponibilidade externa
- **Actuator + métricas** para observabilidade
- Controle de concorrência com `@Version`
- Geração de número de conta com algoritmo de validação

---
## Estratégia de testes

O projeto também evidencia preocupação com qualidade de software por meio de testes automatizados em diferentes camadas.

### Cobertura atual

- **Testes de contexto** com `@SpringBootTest` para validar a inicialização dos microsserviços
- **Testes unitários de service** com **JUnit 5** e **Mockito**, cobrindo regras de negócio, validações, propagação de exceções e persistência
- **Testes web com `@WebMvcTest` + `MockMvc`**, cobrindo endpoints, status HTTP e payloads JSON
- **Testes do `GlobalExceptionHandler`**, validando respostas padronizadas para erros de negócio, validação, JSON malformado, indisponibilidade externa e recursos não encontrados
- Padrão replicado no microsserviço de **clientes** e no microsserviço de **contas**

### O que esses testes reforçam

- confiabilidade da camada de serviço
- previsibilidade das respostas da API
- padronização do tratamento de erros
- segurança para evoluir regras de negócio e endpoints
- maturidade técnica além do CRUD funcional

---

## Stack utilizada

### Backend
- Java 21
- Spring Boot 3.5.9
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cloud OpenFeign
- Resilience4j
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger UI

### Banco de dados
- H2 Database

### Infra
- Docker
- Docker Compose
- Maven Wrapper

---

## Endpoints principais

### Serviço de clientes — porta `8080`
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/clientes` | Lista clientes com paginação |
| GET | `/clientes/{id}` | Busca cliente por ID |
| POST | `/clientes` | Cria cliente |
| PUT | `/clientes/{id}` | Atualiza cliente |
| PATCH | `/clientes/{id}/deactivate` | Desativa cliente |
| DELETE | `/clientes/{id}` | Remove cliente |

### Serviço de contas — porta `8081`
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/contas` | Lista contas com paginação |
| GET | `/contas/{id}` | Busca conta por ID |
| POST | `/contas` | Cria conta |
| PATCH | `/contas/{id}` | Atualiza tipo/status da conta |
| DELETE | `/contas/{id}` | Remove conta |

---

## Documentação das APIs

Após subir os serviços, a documentação pode ser acessada em:

### Swagger UI
- Cliente: `http://localhost:8080/swagger-ui/index.html`
- Conta: `http://localhost:8081/swagger-ui/index.html`

### H2 Console
- Cliente: `http://localhost:8080/h2-console`
- Conta: `http://localhost:8081/h2-console`

### Actuator
- Conta: `http://localhost:8081/actuator/health`

---

## Como executar o projeto

### Opção 1 — com Docker Compose
Na raiz do repositório:

```bash
docker compose up --build
```

Isso sobe:
- `cliente` na porta **8080**
- `conta-transf` na porta **8081**

---

### Opção 2 — execução local com Maven Wrapper

#### Subindo o serviço de clientes
```bash
cd cliente
./mvnw spring-boot:run
```

No Windows:
```bash
cd cliente
mvnw.cmd spring-boot:run
```

#### Subindo o serviço de contas
```bash
cd conta-transf
./mvnw spring-boot:run
```

No Windows:
```bash
cd conta-transf
mvnw.cmd spring-boot:run
```

---
### Como executar os testes

#### Serviço de clientes
```bash
cd cliente
./mvnw test
```

No Windows:
```bash
cd cliente
mvnw.cmd test
```

#### Serviço de contas
```bash
cd conta-transf
./mvnw test
```

No Windows:
```bash
cd conta-transf
mvnw.cmd test
```

---

## Exemplos de uso

### Criar cliente
```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Souza",
    "cpf": "123.456.789-10",
    "email": "ana@email.com",
    "birthDate": "10/05/1995"
  }'
```

### Criar conta para um cliente existente
```bash
curl -X POST http://localhost:8081/contas \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "type": "CORRENTE"
  }'
```

### Atualizar status/tipo da conta
```bash
curl -X PATCH http://localhost:8081/contas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "BLOQUEADA",
    "type": "POUPANCA"
  }'
```

---


## Boas práticas aplicadas

- Separação em camadas: **controller, service, repository, DTO, entity**
- Validação de entrada no backend
- Tratamento centralizado de exceções
- Respostas organizadas para erros de negócio
- Paginação com `Pageable`
- Integração entre serviços com tolerância a falhas
- Testes automatizados em camadas críticas da aplicação
- Estrutura pronta para evolução do domínio
- Ambiente simples para avaliação técnica e demonstração em portfólio

---

## Futuras evoluções

Algumas evoluções naturais para este projeto:

- autenticação e autorização com JWT
- transferências entre contas
- extrato bancário
- testes de integração com Testcontainers
- persistência em PostgreSQL
- mensageria para eventos financeiros
- pipeline CI/CD
- monitoramento com Prometheus + Grafana

---
