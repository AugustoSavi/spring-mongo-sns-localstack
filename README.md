# Restaurant Marketplace

Este projeto consiste na implementação de uma API para o desafio da AnotaAi no canal da Fernanda Kipper. A API é desenvolvida utilizando as seguintes tecnologias:

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
[![Licença](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

## Sumário

- [Instalação](#instalação)
- [Configuração](#configuração)
- [Utilização](#utilização)
- [Pontos de Extensão da API](#pontos-de-extensão-da-api)
- [Contribuições](#contribuições)

## Instalação

1. Clone o repositório:

2. Instale as dependências com o Maven

3. Inicie os serviços com o Docker Compose

```bash
sudo docker-compose up
```

4. Execute o seguinte comando abaixo para criar o tópico SNS

```bash
sudo docker-compose exec localstack aws --endpoint-url http://localhost:4566 sns create-topic --name catalog-emit
```

#### A execução retornará:

```json
{
  "TopicArn": "arn:aws:sns:us-east-1:000000000000:catalog-emit"
}
```

5. Execute o seguinte comando abaixo para criar a fila SQS

```bash
sudo docker-compose exec localstack aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name catalog-update
```

#### A execução retornará:

```json
{
  "QueueUrl": "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update"
}
```

6. Obtenha o ARN da fila SQS:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update --attribute-names QueueArn --output text
```

#### A execução retornará:

```
ATTRIBUTES      arn:aws:sqs:us-east-1:000000000000:catalog-update
```

7. Inscreva a Fila SQS no Tópico SNS

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:catalog-emit --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:catalog-update
```

#### A execução retornará:

```json
{
  "SubscriptionArn": "arn:aws:sns:us-east-1:000000000000:catalog-emit:d6e263f7-cdce-46cb-af85-88366c5278a2"
}
```

8. Crie a função Lambda

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda create-function --function-name catalogEmitConsumer --zip-file fileb:///lambda/function.zip --handler index.handler --runtime nodejs20.x --role arn:aws:iam::123456789012:role/irrelevant
```

#### execução ira retornará:

```json
{
    "UUID": "e61f8b0c-121b-4d1c-8f51-76b768f9da2f",
    "BatchSize": 1,
    "MaximumBatchingWindowInSeconds": 0,
    "EventSourceArn": "arn:aws:sqs:us-east-1:000000000000:catalog-update",
    "FunctionArn": "arn:aws:lambda:us-east-1:000000000000:function:catalogEmitConsumer",
    "LastModified": 1706460950.702148,
    "State": "Creating",
    "StateTransitionReason": "USER_INITIATED",
    "FunctionResponseTypes": []
}
```

Atualize o código da função, se necessário:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda update-function-code --function-name catalogEmitConsumer --zip-file fileb:///lambda/function.zip
```

#### A execução retornará:

```json
{
  "FunctionName": "catalogEmitConsumer",
  "FunctionArn": "arn:aws:lambda:us-east-1:000000000000:function:catalogEmitConsumer",
  "Runtime": "nodejs20.x",
  "Role": "arn:aws:iam::123456789012:role/irrelevant",
  "Handler": "index.handler",
  "CodeSize": 337,
  "Description": "",
  "Timeout": 3,
  "MemorySize": 128,
  "LastModified": "2024-01-28T05:46:50.095935+0000",
  "CodeSha256": "DmiYO1hAx9WEMWEmHHE8LyZmXzrX3Xtm5wO6J2HJAr8=",
  "Version": "$LATEST",
  "TracingConfig": {
    "Mode": "PassThrough"
  },
  "RevisionId": "c3e8579a-1a9e-4bf2-984b-fe700dae54e9",
  "State": "Pending",
  "StateReason": "The function is being created.",
  "StateReasonCode": "Creating",
  "PackageType": "Zip",
  "Architectures": [
    "x86_64"
  ],
  "EphemeralStorage": {
    "Size": 512
  },
  "SnapStart": {
    "ApplyOn": "None",
    "OptimizationStatus": "Off"
  },
  "RuntimeVersionConfig": {
    "RuntimeVersionArn": "arn:aws:lambda:us-east-1::runtime:8eeff65f6809a3ce81507fe733fe09b835899b99481ba22fd75b5a7338290ec1"
  }
}
```

9. Configure a Função Lambda para ser Acionada pela Fila SQS

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda create-event-source-mapping --function-name catalogEmitConsumer --batch-size 1 --event-source-arn arn:aws:sqs:us-east-1:000000000000:catalog-update
```

10. Crie o bucket S3

````bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket anotaai-catalog-marketplace
````

#### A execução retornará:

```json
{
    "Location": "/anotaai-catalog-marketplace"
}
```

11. Listar objetos do bucket s3:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 s3api list-objects --bucket anotaai-catalog-marketplace
```

#### A execução retornará:

```json
{
    "Contents": [
        {
            "Key": "4444444-catalog.json",
            "LastModified": "2024-01-28T17:00:25.000Z",
            "ETag": "\"3b167696e141ec0b36de289ccaec466a\"",
            "Size": 264,
            "StorageClass": "STANDARD",
            "Owner": {
                "DisplayName": "webfile",
                "ID": "75aa57f09aa0c8caeab4f8c24e99d10f8e7faeebf76c078efc7c6caea54ba06a"
            }
        }
    ],
    "RequestCharged": null
}
```

12. ver o conteudo de um objeto:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 s3api get-object --bucket anotaai-catalog-marketplace --key 4444444-catalog.json /temp/catalog.json
```

### Teste de envio de mensagem via linha de comando

1. **Publicar uma Mensagem no Tópico SNS:**

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sns publish --topic-arn arn:aws:sns:us-east-1:000000000000:catalog-emit --message "Test message"
```

2. **Verificar a Mensagem na Fila SQS:**

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update
```

## Chamar função manualmente:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda invoke --function-name catalogEmitConsumer /tmp/response.json
```

## Verificar o retorno

```bash
sudo docker-compose exec localstack cat /tmp/response.json
```

3. Crie uma configuração com suas variáveis de ambiente de execução da AWS que são usadas no arquivo `application.properties`

```yaml
aws.region=us-east-1
aws.accessKeyId=${AWS_KEY_ID}
aws.secretKey=${AWS_SECRET}
```

**Valores de Configuração**

```yaml
AWS_KEY_ID=VALOR1;AWS_SECRET=VALOR2
```

## Utilização

1. Inicie a aplicação com o Maven
2. A API estará acessível em http://localhost:8080

## Pontos de Extensão da API
A API fornece os seguintes pontos de extensão:

**API PRODUTO**
```markdown
POST /api/product - Crie um novo produto
GET /api/product - Recupere todos os produtos
PUT /api/product/{id} - Atualize um produto
DELETE /api/product/{id} - Exclua um produto
```

**CORPO**
```json
{
  "title": "Produto para postar no tópico",
  "description": "",
  "ownerId": "4444444",
  "categoryId": "659d558b0304df732ddd4587",
  "price": 10000
}
```

**API CATEGORIA**
```markdown
POST /api/category - Crie uma nova categoria
GET /api/category - Recupere todas as categorias
PUT /api/category/{id} - Atualize uma categoria
DELETE /api/category/{id} - Exclua uma categoria
```

**CORPO**
```json
{
  "id": "393948882828",
  "title": "Teste",
  "description": "",
  "ownerId": "4444444"
}
```

## Contribuições

Contribuições são bem-vindas! Se você encontrar algum problema ou tiver sugestões de melhorias, por favor, abra uma issue ou envie um pull request para o repositório.

Ao contribuir para este projeto, siga o estilo de código existente, as [convenções de commit](https://www.conventionalcommits.org/en/v1.0.0/), e envie suas alterações em um branch separado.