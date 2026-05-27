# OrbitCast API

Backend da solução **OrbitCast**, uma API para planejamento e simulação de transmissões via satélite em regiões remotas ou com baixa conectividade.

A aplicação permite cadastrar clientes, canais, regiões, campanhas de transmissão, planos de cobertura e simulações. O objetivo é apoiar decisões sobre custo estimado, alcance, qualidade de sinal e viabilidade operacional antes da execução de uma campanha.

API em produção:

```text
https://orbitcast.onrender.com
```

A rota raiz `/` não possui endpoint e pode retornar `404`. Para validar se a API está online, use `/health`.

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
- Maven
- H2 em memória para desenvolvimento local
- Oracle Database para ambiente externo
- Docker para deploy
- Render para hospedagem

## Organização do projeto

- `entities`: classes de domínio.
- `connection`: conexão e inicialização do banco.
- `dao`: acesso JDBC ao banco.
- `bo`: regras de negócio e validações.
- `resources`: recursos REST expostos pela API.
- `exceptions`: tratamento padronizado de erros.
- `src/main/resources/db`: scripts do banco local H2.
- `src/main/resources/db/oracle`: scripts do banco Oracle.

## Requisitos

- Java 21 instalado.
- Maven instalado.
- Docker instalado apenas para testar imagem ou deploy local em container.
- Banco Oracle configurado apenas quando for rodar com o perfil `oracle`.

## Rodando localmente

Execute:

```shell
mvn quarkus:dev
```

A API local ficará disponível em:

```text
http://localhost:8080
```

Health check:

```text
GET /health
```

Resposta esperada:

```text
OrbitCast API online
```

## Banco local

No modo padrão, a aplicação usa H2 em memória e inicializa as tabelas automaticamente ao subir.

Scripts usados localmente:

- `src/main/resources/db/schema.sql`
- `src/main/resources/db/data.sql`

## Banco Oracle

Para executar com Oracle, configure as variáveis de ambiente antes de iniciar a aplicação.

Exemplo no PowerShell:

```powershell
$env:ORACLE_JDBC_URL="jdbc:oracle:thin:@//host:porta/service_name"
$env:ORACLE_USERNAME="seu_usuario"
$env:ORACLE_PASSWORD="sua_senha"
$env:ORBITCAST_DATABASE_INITIALIZE="false"
```

Execute com o perfil Oracle:

```powershell
mvn quarkus:dev -Dquarkus.profile=oracle
```

Scripts do Oracle:

- `src/main/resources/db/oracle/drop.sql`
- `src/main/resources/db/oracle/schema.sql`
- `src/main/resources/db/oracle/data.sql`

Use `ORBITCAST_DATABASE_INITIALIZE=true` apenas com banco vazio, pois a aplicação tentará executar os scripts de criação e carga inicial ao iniciar.

## Deploy no Render

O deploy está preparado para ser feito como **Web Service** no Render usando Docker.

Configuração principal:

```text
Runtime: Docker
Branch: main
Root Directory: vazio
Dockerfile Path: ./Dockerfile
```

Variáveis de ambiente no Render:

```text
QUARKUS_PROFILE=oracle
ORACLE_JDBC_URL=jdbc:oracle:thin:@//host:porta/service_name
ORACLE_USERNAME=seu_usuario
ORACLE_PASSWORD=sua_senha
ORBITCAST_DATABASE_INITIALIZE=false
```

Não configure `PORT` manualmente no Render. A própria plataforma fornece essa variável.

O banco Oracle precisa estar acessível pela internet para que o Render consiga conectar. Uma URL com `localhost` funciona apenas na máquina local.

Depois do deploy, valide:

```text
GET /health
GET /dashboard/resumo
GET /clientes
```

## Contrato da API

Base local:

```text
http://localhost:8080
```

Base em produção:

```text
https://orbitcast.onrender.com
```

Formato das requisições com corpo:

```text
Content-Type: application/json
```

O CORS está habilitado para consumo pelo front-end.

## Endpoints

### Health

```text
GET /health
```

### Dashboard

```text
GET /dashboard/resumo
```

Exemplo de resposta:

```json
{
  "totalClientes": 6,
  "totalCanais": 7,
  "totalRegioes": 10,
  "totalCampanhas": 6,
  "totalSimulacoes": 8,
  "alcanceEstimadoTotal": 4684018,
  "custoMedioSimulacoes": 752737.5,
  "qualidadeMediaSinal": 83.49125,
  "campanhasPorStatus": {
    "APROVADA": 2,
    "EM_ANALISE": 2,
    "PLANEJADA": 2
  },
  "simulacoesPorViabilidade": {
    "MEDIA": 8
  }
}
```

### Clientes

```text
GET /clientes
GET /clientes/{id}
POST /clientes
PUT /clientes/{id}
DELETE /clientes/{id}
```

Exemplo para `POST /clientes`:

```json
{
  "nome": "Instituto Conexao Remota",
  "documento": "77700700000113",
  "email": "contato@conexaoremota.org",
  "telefone": "(11) 4000-7000",
  "segmento": "Educacao",
  "ativo": true
}
```

### Canais

```text
GET /canais
GET /canais/{id}
POST /canais
PUT /canais/{id}
DELETE /canais/{id}
```

Exemplo para `POST /canais`:

```json
{
  "clienteId": 13,
  "nome": "Aulas Orbitais",
  "tipoConteudo": "Educacional",
  "publicoAlvo": "Estudantes de regioes remotas",
  "classificacaoIndicativa": "Livre",
  "ativo": true
}
```

### Regiões

```text
GET /regioes
GET /regioes/{id}
POST /regioes
PUT /regioes/{id}
DELETE /regioes/{id}
```

Exemplo para `POST /regioes`:

```json
{
  "nome": "Comunidade Ribeirinha Norte",
  "estado": "AM",
  "pais": "Brasil",
  "populacaoEstimada": 45000,
  "indiceConectividade": 22.5,
  "latitude": -3.1019,
  "longitude": -60.025,
  "areaKm2": 12000.0,
  "prioridadeSocial": 5
}
```

### Campanhas

```text
GET /campanhas
GET /campanhas/{id}
POST /campanhas
PUT /campanhas/{id}
DELETE /campanhas/{id}
GET /campanhas/{id}/regioes
POST /campanhas/{id}/regioes/{regiaoId}
DELETE /campanhas/{id}/regioes/{regiaoId}
GET /campanhas/{id}/simulacoes
POST /campanhas/{id}/simulacoes
GET /campanhas/{id}/planos-cobertura
```

Valores aceitos em `qualidadeDesejada`:

```text
SD
HD
FULL_HD
4K
```

Valores aceitos em `status`:

```text
PLANEJADA
EM_ANALISE
APROVADA
CANCELADA
FINALIZADA
```

Exemplo para `POST /campanhas`:

```json
{
  "clienteId": 13,
  "canalId": 10,
  "nome": "Aulas via Satelite",
  "descricao": "Campanha educacional para regioes com baixa conectividade.",
  "dataInicio": "2026-06-15",
  "dataFim": "2026-06-20",
  "duracaoHoras": 40,
  "qualidadeDesejada": "HD",
  "orcamento": 450000.0,
  "status": "PLANEJADA"
}
```

Exemplo para `POST /campanhas/{id}/regioes/{regiaoId}`:

```json
{
  "prioridade": 5,
  "observacao": "Regiao prioritaria pela baixa conectividade."
}
```

O endpoint `POST /campanhas/{id}/simulacoes` não recebe corpo. A simulação é gerada com base nos dados da campanha e das regiões associadas.

Exemplo:

```shell
curl -X POST https://orbitcast.onrender.com/campanhas/9/simulacoes
```

Exemplo de resposta:

```json
{
  "id": 19,
  "campanhaId": 9,
  "custoEstimado": 1202000.0,
  "alcanceEstimado": 727200,
  "qualidadeSinal": 80.67,
  "viabilidade": "MEDIA",
  "recomendacao": "Revisar plano: considere reduzir regioes, ajustar qualidade ou ampliar o orcamento antes da aprovacao.",
  "dataSimulacao": "2026-05-26T20:29:08.87308"
}
```

### Simulações

```text
GET /simulacoes
GET /simulacoes/{id}
GET /campanhas/{id}/simulacoes
POST /campanhas/{id}/simulacoes
```

### Planos de cobertura

```text
GET /planos-cobertura
GET /planos-cobertura/{id}
POST /planos-cobertura
PUT /planos-cobertura/{id}
DELETE /planos-cobertura/{id}
GET /campanhas/{id}/planos-cobertura
```

Valores aceitos em `viabilidadeGeral`:

```text
BAIXA
MEDIA
ALTA
```

Exemplo para `POST /planos-cobertura`:

```json
{
  "campanhaId": 9,
  "nome": "Plano Educacional Reforcado",
  "descricao": "Prioriza regioes com baixa conectividade e alto impacto educacional.",
  "custoTotal": 430000.0,
  "alcanceTotal": 535000,
  "viabilidadeGeral": "ALTA"
}
```

## Formato de erro

Exemplo:

```json
{
  "status": 400,
  "erro": "Regra de negocio violada",
  "mensagem": "Nome do cliente e obrigatorio.",
  "timestamp": "2026-05-26T15:00:00"
}
```

Status usados com mais frequência:

- `400`: regra de negócio ou validação violada.
- `404`: recurso não encontrado ou rota inexistente.
- `500`: erro interno ou falha de banco de dados.

## Testes

Execute:

```shell
mvn test
```

Também é possível validar a API em produção com:

```text
GET https://orbitcast.onrender.com/health
GET https://orbitcast.onrender.com/dashboard/resumo
GET https://orbitcast.onrender.com/clientes
```
