# Sistema Turismo

Sistema de Gestão Turística — Spring Boot (Java 21) + Thymeleaf + MySQL.

## Pré-requisitos

- Java 21
- Docker e Docker Compose

## Passo a passo

### 1. Subir o banco de dados

O projeto usa MySQL, com conexão configurada em [`ConnectionFactory`](src/main/java/br/com/agencia/config/ConnectionFactory.java) (usuário `root`, senha `root`, banco `agencia_turismo`, porta `3306`).

```bash
docker compose up -d
```

Isso sobe um container MySQL e, na primeira inicialização, já executa automaticamente:
- [`schema.sql`](src/main/resources/schema.sql) — cria o banco e as tabelas
- [`seed.sql`](src/main/resources/seed.sql) — insere dados de exemplo

> Os scripts de inicialização só rodam quando o volume de dados está vazio. Se precisar recriar o banco do zero, remova o volume antes de subir novamente:
> ```bash
> docker compose down -v
> docker compose up -d
> ```

### 2. Rodar a aplicação

Com o banco no ar, suba a aplicação usando o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Ou, para gerar e rodar o `.jar`:

```bash
./mvnw clean package
java -jar target/sistema-turismo-0.0.1-SNAPSHOT.jar
```

A aplicação sobe por padrão em `http://localhost:8080`.

### 3. Parar tudo

```bash
docker compose down
```
