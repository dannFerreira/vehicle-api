# Vehicle API

API REST desenvolvida para o Tech Challenge da PósTech SOAT - Fase 3.

A solução permite cadastrar, editar, listar e comprar veículos, mantendo os dados de autenticação dos usuários separados dos dados transacionais da aplicação.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- PostgreSQL
- Flyway
- Amazon Cognito
- Docker
- Docker Compose
- GitHub Actions
- AWS EC2

## Arquitetura

A solução foi dividida em responsabilidades separadas:

- `vehicle-api`: API e regras de negócio
- `vehicle-auth`: autenticação e autorização utilizando Amazon Cognito
- PostgreSQL: armazenamento dos veículos e vendas
- AWS EC2: execução da aplicação
- GitHub Actions: CI/CD

Os usuários e credenciais ficam armazenados no Amazon Cognito, separados do banco transacional da aplicação.

A API recebe tokens JWT emitidos pelo Cognito e utiliza os grupos do usuário para autorização.

### Perfis

- `ADMIN`: pode cadastrar e editar veículos
- `USER`: pode comprar veículos
- As consultas de veículos disponíveis e vendidos são públicas

## Funcionalidades

### Veículos

- Cadastro de veículo
- Edição de veículo
- Listagem de veículos disponíveis
- Listagem de veículos vendidos
- Ordenação por preço crescente

Dados do veículo:

- marca
- modelo
- ano
- cor
- preço
- status

### Compra

Usuários autenticados podem comprar veículos disponíveis.

Ao realizar uma compra:

1. o veículo é localizado;
2. é validado se ainda está disponível;
3. uma venda é registrada;
4. o veículo passa para o status `SOLD`.

A operação utiliza lock pessimista para evitar que o mesmo veículo seja vendido simultaneamente para dois usuários.

## Endpoints

### Listar veículos disponíveis

```http
GET /vehicles
```

Não requer autenticação.

### Listar veículos vendidos

```http
GET /vehicles/sold
```

Não requer autenticação.

### Cadastrar veículo

```http
POST /vehicles
```

Requer usuário pertencente ao grupo `ADMIN`.

Exemplo:

```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2022,
  "color": "Black",
  "price": 120000.00
}
```

### Editar veículo

```http
PUT /vehicles/{id}
```

Requer usuário pertencente ao grupo `ADMIN`.

Exemplo:

```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2023,
  "color": "White",
  "price": 125000.00
}
```

### Comprar veículo

```http
POST /vehicles/{id}/purchase
```

Requer usuário pertencente ao grupo `USER`.

O comprador é identificado através do `sub` presente no token JWT do Amazon Cognito.

## Autenticação

A autenticação é realizada através do Amazon Cognito.

O Cognito é responsável por:

- cadastro dos usuários;
- autenticação;
- emissão de tokens JWT;
- separação dos usuários em grupos.

Os grupos utilizados são:

```text
USER
ADMIN
```

O token JWT contém o claim:

```json
{
  "cognito:groups": ["USER"]
}
```

A aplicação converte esses grupos em authorities do Spring Security:

```text
USER  -> ROLE_USER
ADMIN -> ROLE_ADMIN
```

## Banco de dados

O PostgreSQL armazena apenas dados transacionais.

As principais tabelas são:

### `vehicles`

Armazena os veículos cadastrados.

### `sales`

Armazena as compras realizadas.

Os usuários não são armazenados no PostgreSQL. A referência ao comprador é feita utilizando o identificador `sub` fornecido pelo Cognito.

As alterações de schema são controladas pelo Flyway.

## Executando localmente

### Pré-requisitos

- Java 21
- Docker
- Docker Compose

### Build

Linux/macOS:

```bash
./mvnw package
```

Windows:

```powershell
.\mvnw.cmd package
```

### Subir os serviços

```bash
docker compose up -d --build
```

A API ficará disponível em:

```text
http://localhost:8080
```

Health check:

```http
GET /actuator/health
```

Resposta esperada:

```json
{
  "status": "UP"
}
```

## Configuração do Cognito

A aplicação utiliza o issuer do Amazon Cognito no formato:

```text
https://cognito-idp.us-east-2.amazonaws.com/<USER_POOL_ID>
```

A configuração pode ser sobrescrita através da variável de ambiente:

```text
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI
```

## Testes

Para executar os testes:

Linux/macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

O projeto possui testes automatizados para:

- regras de negócio de veículos;
- regras de compra;
- controllers;
- validações;
- carregamento do contexto Spring.

Os testes também são executados automaticamente no GitHub Actions.

## CI/CD

O projeto utiliza GitHub Actions.

O fluxo adotado é:

```text
Feature Branch
      |
      v
Pull Request
      |
      v
Continuous Integration
      |
      | testes + build
      v
Merge na main
      |
      v
Continuous Deployment
      |
      v
AWS EC2
```

O pipeline de CI executa:

- build da aplicação;
- testes automatizados;
- validação da aplicação.

Após merge na branch `main`, o pipeline de CD:

1. gera o arquivo `.jar`;
2. conecta na instância AWS EC2;
3. envia os arquivos necessários para o servidor;
4. executa o build da imagem Docker;
5. atualiza os containers através do Docker Compose.

Dessa forma, as alterações da aplicação são realizadas através de Pull Requests e práticas de CI/CD.

## Infraestrutura

A aplicação está hospedada na AWS.

Na instância EC2 são executados containers Docker para:

```text
Vehicle API
PostgreSQL
```

A autenticação é fornecida externamente pelo Amazon Cognito.

Arquitetura simplificada:

```text
              Amazon Cognito
                    |
                    | JWT
                    v
Client ------> Vehicle API
                    |
                    v
               PostgreSQL
                    ^
                    |
                 AWS EC2
```

## Fluxo de compra

Fluxo ponta a ponta da solução:

```text
1. Cliente é cadastrado no Amazon Cognito
2. Cliente realiza autenticação
3. Cognito retorna um token JWT
4. Administrador cadastra um veículo
5. Veículo fica disponível com status AVAILABLE
6. Cliente autenticado realiza a compra
7. Venda é registrada no PostgreSQL
8. Veículo passa para o status SOLD
9. Veículo deixa de aparecer na listagem de disponíveis
10. Veículo passa a aparecer na listagem de vendidos
```

## Repositórios

### Vehicle API

Contém:

- código-fonte da API;
- regras de negócio;
- testes;
- migrations do Flyway;
- Dockerfile;
- Docker Compose;
- workflows de CI/CD.

### Vehicle Auth

Contém a infraestrutura de autenticação e autorização utilizando Terraform e Amazon Cognito.

## Autor

Projeto desenvolvido para o Tech Challenge - PósTech SOAT - Fase 3.