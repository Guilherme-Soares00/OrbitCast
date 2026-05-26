# OrbitCast API

Backend da solução **OrbitCast**.

O OrbitCast é uma plataforma de planejamento e simulação de transmissões via satélite para regiões remotas ou com baixa conectividade. A API permite cadastrar clientes, canais, regiões, campanhas, associar regiões a campanhas e gerar simulações de custo, alcance, qualidade de sinal e viabilidade.

## Equipe

Turma: 1TDSPB

- Gabriel Stuani - RM: 566682
- Guilherme Soares - RM: 568227
- Erick Ramos - RM: 567837
- Matheus Carneiro Maciel - RM: 567753

## Tecnologias

- Java 21
- Quarkus 3.35.4
- Jakarta REST
- JDBC manual
- H2 em memória para desenvolvimento local
- Maven

## Camadas

- `entities`: classes de domínio.
- `connection`: conexão e inicialização do banco.
- `dao`: acesso JDBC ao banco.
- `bo`: regras de negócio e validações.
- `resources`: endpoints REST.
- `exceptions`: tratamento padronizado de erros.

## Rodando Localmente

```shell
mvn quarkus:dev
```

A API fica disponível em:

```text
http://localhost:8080
```

Health check:

```text
GET /health
```

## Banco Local

O banco H2 em memória é inicializado automaticamente ao subir a aplicação.

Scripts:

- `src/main/resources/db/schema.sql`
- `src/main/resources/db/data.sql`

## Deploy No Render

Crie um Web Service no Render a partir do repositório do GitHub e selecione runtime Docker. O projeto possui um `Dockerfile` na raiz.

Variáveis de ambiente para usar Oracle:

```text
QUARKUS_PROFILE=oracle
ORACLE_JDBC_URL=jdbc:oracle:thin:@//host:porta/service_name
ORACLE_USERNAME=seu_usuario
ORACLE_PASSWORD=sua_senha
ORBITCAST_DATABASE_INITIALIZE=false
```

O banco Oracle precisa estar acessível pela internet para o Render conseguir conectar. Uma URL com `localhost` só funciona na sua máquina, não no servidor do Render.

Depois do deploy, valide:

```text
GET /health
GET /dashboard/resumo
```

## Endpoints Principais

Contrato detalhado com exemplos para o front-end:

- `API_CONTRACT.md`

### Clientes

- `GET /clientes`
- `GET /clientes/{id}`
- `POST /clientes`
- `PUT /clientes/{id}`
- `DELETE /clientes/{id}`

### Canais

- `GET /canais`
- `GET /canais/{id}`
- `POST /canais`
- `PUT /canais/{id}`
- `DELETE /canais/{id}`

### Regiões

- `GET /regioes`
- `GET /regioes/{id}`
- `POST /regioes`
- `PUT /regioes/{id}`
- `DELETE /regioes/{id}`

### Campanhas

- `GET /campanhas`
- `GET /campanhas/{id}`
- `POST /campanhas`
- `PUT /campanhas/{id}`
- `DELETE /campanhas/{id}`
- `GET /campanhas/{id}/regioes`
- `POST /campanhas/{id}/regioes/{regiaoId}`
- `DELETE /campanhas/{id}/regioes/{regiaoId}`
- `GET /campanhas/{id}/planos-cobertura`

### Simulações

- `GET /simulacoes`
- `GET /simulacoes/{id}`
- `GET /campanhas/{id}/simulacoes`
- `POST /campanhas/{id}/simulacoes`

### Planos De Cobertura

- `GET /planos-cobertura`
- `GET /planos-cobertura/{id}`
- `POST /planos-cobertura`
- `PUT /planos-cobertura/{id}`
- `DELETE /planos-cobertura/{id}`

### Dashboard

- `GET /dashboard/resumo`

## Exemplo De Simulação

```shell
curl -X POST http://localhost:8080/campanhas/1/simulacoes
```

Resposta esperada:

```json
{
  "campanhaId": 1,
  "custoEstimado": 1202000.00,
  "alcanceEstimado": 727200,
  "qualidadeSinal": 80.67,
  "viabilidade": "MEDIA",
  "recomendacao": "Revisar plano: considere reduzir regioes, ajustar qualidade ou ampliar o orcamento antes da aprovacao."
}
```

## Testes

```shell
mvn test
```
