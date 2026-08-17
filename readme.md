# iCompras Microservices

Sistema de gerenciamento de pedidos de e-commerce construído com arquitetura de microsserviços, utilizando Apache Kafka para comunicação assíncrona entre serviços.

---

## Arquitetura

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│ Clientes │     │ Produtos │     │ Servico  │
│  :8082   │     │  :8081   │     │ Bancario │
└────┬─────┘     └────┬─────┘     └────┬─────┘
     │  Feign         │  Feign         │  Feign
     └────────┬───────┴────────────────┘
              │
       ┌──────▼──────┐
       │   Pedidos   │
       │    :8080    │
       └──────┬──────┘
              │ Kafka: pedidos-pagos
       ┌──────▼──────┐
       │ Faturamento │──── MinIO (PDF)
       │    :8083    │
       └──────┬──────┘
              │ Kafka: pedidos-faturados
       ┌──────▼──────┐
       │  Logistica  │
       │    :8084    │
       └──────┬──────┘
              │ Kafka: pedidos-enviados
       ┌──────▼──────┐
       │   Pedidos   │ (atualização de status)
       └─────────────┘
```

### Fluxo de um Pedido

```
1. Cliente cria pedido (POST /pedidos)
2. Pedidos valida cliente e produtos via Feign
3. Pedidos solicita pagamento ao Servico Bancario (stub)
4. Sistema bancario notifica pagamento via webhook (POST /pedidos/callback-pagamentos)
5. Pedidos publica evento "pedido-pago" no Kafka
6. Faturamento recebe evento, gera PDF da nota fiscal e envia para MinIO
7. Faturamento publica evento "pedido-faturado" no Kafka
8. Logistica recebe evento, gera codigo de rastreio
9. Logistica publica evento "pedido-enviado" no Kafka
10. Pedidos recebe atualizacao de status e persiste
```

### Ciclo de Vida do Status do Pedido

```
REALIZADO → PAGO → FATURADO → ENVIADO
    └──→ ERRO_PAGAMENTO
```

---

## Microsserviços

| Serviço | Porta | Descrição | Banco de Dados |
|---|---|---|---|
| `clientes` | 8082 | CRUD de clientes com validação de CPF | `icomprasclientes` |
| `produtos` | 8081 | CRUD do catálogo de produtos | `icomprasprodutos` |
| `pedidos` | 8080 | Orquestrador central — cria pedidos, valida, solicita pagamento | `icompraspedidos` |
| `faturamento` | 8083 | Gera notas fiscais em PDF e armazena no MinIO | Nenhum (stateless) |
| `logistica` | 8084 | Gera códigos de rastreio para envio | Nenhum (stateless) |
| `icompras-servicos` | — | Infraestrutura (Docker Compose) | — |

---

## Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.0 / 4.1.0 |
| Build | Maven |
| Message Broker | Apache Kafka (Confluent 7.2.15) |
| Banco de Dados | PostgreSQL 17.4 |
| ORM | Spring Data JPA / Hibernate |
| Migrações | Flyway (clientes, produtos) |
| Object Storage | MinIO (S3-compatível) |
| Geração de PDF | JasperReports 7.0.6 |
| Chamadas Síncronas | Spring Cloud OpenFeign |
| Mapeamento de Objetos | MapStruct 1.6.0 |
| Boilerplate | Lombok |
| Validação | Jakarta Bean Validation + Hibernate Validator |
| Testes | JUnit 5, Mockito, Spring Boot Test, AssertJ |

---

## Padrões Arquiteturais

- **Event-Driven Architecture** — comunicação assíncrona via Kafka entre pedidos, faturamento e logística
- **Saga (Choreography)** — orquestração decentralizada do ciclo de vida do pedido
- **Database per Service** — cada serviço com dados possui seu próprio banco PostgreSQL
- **Soft Delete** — exclusão lógica via flag `ativo` em clientes e produtos
- **Value Object** — CPF modelado como objeto de valor com normalização automática
- **Specification Pattern** — consultas dinâmicas com filtros compostos via JPA Specifications
- **Trigram Search** — índices GIN com `pg_trgm` para busca parcial e case-insensitive

---

## Tópicos Kafka

| Tópico | Produtor | Consumidor(es) | Payload |
|---|---|---|---|
| `icompras.pedidos-pagos` | pedidos | faturamento | `DetalhePedidoDTO` |
| `icompras.pedidos-faturados` | faturamento | pedidos, logistica | `AtualizacaoStatusPedidoDTO` |
| `icompras.pedidos-enviados` | logistica | pedidos | `AtualizacaoEnvioPedido` |

---

## Endpoints da API

### Clientes (`:8082`)

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/clientes` | Criar cliente (valida CPF) |
| `GET` | `/clientes` | Listar clientes (filtros: nome, cpf, email + paginação) |
| `GET` | `/clientes/{codigo}` | Buscar cliente por código |
| `PUT` | `/clientes/{codigo}` | Atualizar cliente (completo) |
| `PATCH` | `/clientes/{codigo}` | Atualizar cliente (parcial) |
| `DELETE` | `/clientes/{codigo}` | Soft delete (ativo=false) |

### Produtos (`:8081`)

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/produtos` | Criar produto |
| `GET` | `/produtos` | Listar produtos (filtro: nome + paginação) |
| `GET` | `/produtos/{codigo}` | Buscar produto por código |
| `PUT` | `/produtos/{codigo}` | Atualizar produto (completo) |
| `PATCH` | `/produtos/{codigo}` | Atualizar produto (parcial) |
| `DELETE` | `/produtos/{codigo}` | Soft delete (ativo=false) |

### Pedidos (`:8080`)

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/pedidos` | Criar pedido (valida cliente/produtos, solicita pagamento) |
| `GET` | `/pedidos/{codigo}` | Buscar pedido com dados completos (cliente + produtos) |
| `POST` | `/pedidos/pagamentos` | Adicionar pagamento a pedido existente (retry) |
| `POST` | `/pedidos/callback-pagamentos` | Webhook de notificação de pagamento (requer header `apiKey`) |

---

## Modelos de Dados

### Clientes

```
clientes
├── codigo          BIGSERIAL (PK)
├── nome            VARCHAR(150) NOT NULL
├── cpf             CHAR(11) UNIQUE NOT NULL
├── logradouro      VARCHAR(100)
├── numero          VARCHAR(10)
├── bairro          VARCHAR(100)
├── email           VARCHAR(150)
├── telefone        VARCHAR(20)
└── ativo           BOOLEAN DEFAULT true
```

### Produtos

```
produtos
├── codigo          BIGSERIAL (PK)
├── nome            VARCHAR(100) NOT NULL
├── valor_unitario  DECIMAL(16,2) NOT NULL
└── ativo           BOOLEAN DEFAULT true
```

### Pedidos

```
pedidos
├── codigo           SERIAL (PK)
├── codigo_cliente   BIGINT NOT NULL
├── data_pedido      TIMESTAMP DEFAULT now()
├── chave_pagamento  TEXT
├── observacoes      TEXT
├── status           VARCHAR(20) CHECK (...)
├── total            DECIMAL(16,2) NOT NULL
├── codigo_rastreio  VARCHAR(255)
└── url_nf           TEXT

item_pedido
├── codigo           SERIAL (PK)
├── codigo_pedido    BIGINT FK → pedidos
├── codigo_produto   BIGINT NOT NULL
├── quantidade       INT NOT NULL
└── valor_unitario   DECIMAL(16,2) NOT NULL
```

---

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven (ou usar o wrapper `./mvnw`)

---

## Como Executar

### 1. Iniciar Infraestrutura

```bash
# Banco de dados
cd icompras-servicos/database
docker-compose up -d

# Kafka + Zookeeper + Kafka UI
cd icompras-servicos/broker
docker-compose up -d

# MinIO (Object Storage)
cd icompras-servicos/bucket
docker-compose up -d
```

### 2. Iniciar Serviços (cada um em um terminal)

```bash
# Ordem de inicialização recomendada:

# 1º - Clientes (deve estar rodando antes do pedidos)
cd clientes && ./mvnw spring-boot:run

# 2º - Produtos (deve estar rodando antes do pedidos)
cd produtos && ./mvnw spring-boot:run

# 3º - Pedidos
cd pedidos && ./mvnw spring-boot:run

# 4º - Faturamento
cd faturamento && ./mvnw spring-boot:run

# 5º - Logística
cd logistica && ./mvnw spring-boot:run
```

### 3. Build dos JARs

```bash
cd <serviço> && ./mvnw clean package -DskipTests
```

### 4. Rodar Testes

```bash
cd <serviço> && ./mvnw test
```

---

## Variáveis de Configuração

### Clientes

| Propriedade | Valor |
|---|---|
| `server.port` | `8082` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5555/icomprasclientes` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |

### Produtos

| Propriedade | Valor |
|---|---|
| `server.port` | `8081` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5555/icomprasprodutos` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |

### Pedidos

| Propriedade | Valor |
|---|---|
| `server.port` | `8080` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5555/icompraspedidos` |
| `spring.kafka.bootstrap-servers` | `http://localhost:29092` |
| `spring.kafka.consumer.group-id` | `icompras-atualizacao-pedido` |

### Faturamento

| Propriedade | Valor |
|---|---|
| `server.port` | `8083` |
| `spring.kafka.bootstrap-servers` | `http://localhost:29092` |
| `spring.kafka.consumer.group-id` | `icompras-faturamento` |
| `minio.url` | `http://localhost:9000` |
| `minio.access-key` | `minioadmin` |
| `minio.secret-key` | `minioadmin123` |
| `minio.bucket-name` | `icompras.faturas` |

### Logística

| Propriedade | Valor |
|---|---|
| `server.port` | `8084` |
| `spring.kafka.bootstrap-servers` | `http://localhost:29092` |
| `spring.kafka.consumer.group-id` | `icompras-logistica` |

---

## Serviços de Infraestrutura (Docker)

| Serviço | Imagem | Porta Externa |
|---|---|---|
| PostgreSQL | `postgres:17.4` | `5555` |
| Zookeeper | `confluentinc/cp-zookeeper:7.2.15` | `22181` |
| Kafka | `confluentinc/cp-kafka:7.2.15` | `29092` |
| Kafka UI | `provectuslabs/kafka-ui:v0.7.2` | `8090` |
| MinIO API | `minio/minio` | `9000` |
| MinIO Console | `minio/minio` | `9001` |

---

## Testes

| Serviço | Testes Unitários | Testes de Integração | WebMvc Tests |
|---|---|---|---|
| `clientes` | `ClienteServiceTest`, `ClienteTest` | `ClienteSpecificationIntegrationTest` | `ClienteControllerTest` |
| `produtos` | `ProdutoServiceTest`, `ProdutoTest` | `ProdutoSpecificationIntegrationTest` | `ProdutoControllerTest` |
| `pedidos` | — | — | — |
| `faturamento` | — | — | — |
| `logistica` | — | — | — |

Os serviços `clientes` e `produtos` possuem a cobertura de testes mais abrangente, incluindo validação de CPF, CRUD, soft delete, duplicidade e especificações dinâmicas.

---

## Estrutura do Projeto

```
kafka-microservices/
├── clientes/                    # Microsserviço de clientes
│   ├── src/main/java/.../
│   │   ├── controller/          # REST controllers
│   │   ├── model/               # Entidades JPA
│   │   │   ├── Cliente.java
│   │   │   └── Cpf.java         # Value Object
│   │   ├── repository/          # Spring Data JPA
│   │   ├── service/             # Lógica de negócio
│   │   └── dto/                 # Data Transfer Objects
│   └── src/main/resources/
│       └── db/migration/        # Flyway migrations (3 scripts)
│
├── produtos/                    # Microsserviço de produtos
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── dto/
│   └── src/main/resources/
│       └── db/migration/        # Flyway migrations (3 scripts)
│
├── pedidos/                     # Microsserviço de pedidos (orquestrador)
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── dto/
│   │   ├── client/              # Feign clients (clientes, produtos, banco)
│   │   └── kafka/               # Producers e Consumers
│   └── src/main/resources/
│       └── schema.sql           # DDL do banco de pedidos
│
├── faturamento/                 # Microsserviço de faturamento
│   ├── src/main/java/.../
│   │   ├── kafka/               # Consumer e Producer
│   │   ├── service/             # Geração de PDF (JasperReports)
│   │   └── client/              # MinIO client
│   └── src/main/resources/
│       └── templates/           # Template JasperReports (.jrxml)
│
├── logistica/                   # Microsserviço de logística
│   └── src/main/java/.../
│       └── kafka/               # Consumer e Producer
│
└── icompras-servicos/           # Infraestrutura
    ├── broker/docker-compose.yml
    ├── database/
    │   ├── docker-compose.yml
    │   └── schema.sql
    └── bucket/docker-compose.yml
```

---

## Autor

**Filipe Mota** — [Filipecode](https://github.com/Filipemt)
