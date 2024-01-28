# Restaurant Marketplace

Desenvolvimento da api do desafio da anotaAi no canal da Fernanda Kipper

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

This project is an API built using **Java, Java Spring, AWS Simple Queue Service, Mongo DB and AWS Simple Storage Service.**

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Contributing](#contributing)

## Installation

1. Clone the repository:

2. Install dependencies with Maven

3. start services with docker-compose

```bash
sudo docker-compose up
```

4. Run the following command below to create the topic sns 
```bash
sudo docker-compose exec localstack aws --endpoint-url http://localhost:4566 sns create-topic --name catalog-emit
```

#### Execução ira retornar:
```json
{
"TopicArn": "arn:aws:sns:us-east-1:000000000000:catalog-emit"
}
```

5. Run the following command below to create the sqs

```bash
sudo docker-compose exec localstack aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name catalog-update
```

#### Execução ira retornar:
```json
{
"QueueUrl": "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update"
}
```

6. Obtenha o ARN da fila SQS:

````bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update --attribute-names QueueArn --output text
````

#### Execução ira retornar:
```
ATTRIBUTES      arn:aws:sqs:us-east-1:000000000000:catalog-update
```

7. Subscrever a Fila SQS ao Tópico SNS

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:catalog-emit --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:catalog-update
```

#### Execução ira retornar:
```json
{
    "SubscriptionArn": "arn:aws:sns:us-east-1:000000000000:catalog-emit:d6e263f7-cdce-46cb-af85-88366c5278a2"
}
```

8. Criando a função lambda

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda create-function --function-name catalogEmitConsumer --zip-file fileb:///lambda/function.zip --handler index.handler --runtime nodejs20.x --role arn:aws:iam::123456789012:role/irrelevant
```

atualizar codigo da função
```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda update-function-code --function-name catalogEmitConsumer --zip-file fileb:///lambda/function.zip

```

#### Execução ira retornar:
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


## Chamar função manualmente:

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 lambda invoke --function-name catalogEmitConsumer /tmp/response.json
```

## verificar retorno

```bash
sudo docker-compose exec localstack cat /tmp/response.json
```

### Teste de envio de mensagem via command line

1. **Publicar uma Mensagem no Tópico SNS:**

```bash
sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sns publish --topic-arn arn:aws:sns:us-east-1:000000000000:catalog-emit --message "Test message"
```

2. **Verificar a Mensagem na Fila SQS:**

```bash
 sudo docker-compose exec localstack aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/catalog-update
```

3. Create a configuration with your runtime environment variables with your AWS Credentials that are used in `application.properties`

```yaml
aws.region=us-east-1
aws.accessKeyId=${AWS_KEY_ID}
aws.secretKey=${AWS_SECRET}
```

**Config Values**

```yaml
AWS_KEY_ID=VALUE;AWS_SECRET=VALUE2
```
## Usage

1. Start the application with Maven
2. The API will be accessible at http://localhost:8080

## API Endpoints
The API provides the following endpoints:

**API PRODUCT**
```markdown
POST /api/product - Create a new product
GET /api/product - Retrieve all products
PUT /api/product/{id} - Updates a product
DELETE /api/product/{id} - Delete a product
```

**BODY**
```json
{
  "title": "Produto para postar no tópico",
  "description": "",
  "ownerId": "4444444",
  "categoryId": "659d558b0304df732ddd4587",
  "price": 10000
}
```

**API CATEGORY**
```markdown
POST /api/category - Create a new category
GET /api/category - Retrieve all categories
PUT /api/category/{id} - Updates a category
DELETE /api/category/{id} - Delete a category
```

**BODY**
```json
{
  "id": "393948882828",
  "title": "Teste",
  "description": "",
  "ownerId": "4444444"
}
```

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request to the repository.

When contributing to this project, please follow the existing code style, [commit conventions](https://www.conventionalcommits.org/en/v1.0.0/), and submit your changes in a separate branch.




