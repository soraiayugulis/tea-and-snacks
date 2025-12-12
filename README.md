# tea-and-snacks

API to manage maddox bar tea and snacks catalog.

## Starting db

first thing first, start the PostgreSQL database:

```shell script
docker-compose up -d
```

## Running the application in dev mode

run in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

## API Endpoints

### 🌱 Seed Data

initialize or reset the database with sample data:

#### Initialize Database
```bash
curl -X POST http://localhost:8080/api/seed/initialize -H 'Content-Type: application/json'
```
Response: `{"message":"Base de dados inicializada com sucesso","seeded":true}`

#### Reset Database
```bash
curl -X POST http://localhost:8080/api/seed/reset -H 'Content-Type: application/json'
```
Response: `{"message":"Base de dados resetada com sucesso","seeded":false}`

#### Check Status
```bash
curl -X GET http://localhost:8080/api/seed/status
```
Response: `{"seeded":true}` or `{"seeded":false}`

#### Get All Seed Data
```bash
curl -X GET http://localhost:8080/api/seed/data
```
Response (if seeded):
```json
{
  "teas": [],
  "snacks": [],
  "sauces": [],
  "total": {
    "teas": 4,
    "snacks": 3,
    "sauces": 5
  }
}
```
Response (if not seeded): `{"message":"Database not seeded"}`

---

### 🍵 Teas

#### List all teas (paginated - default)
```bash
curl -X GET http://localhost:8080/teas
```

#### List all teas (without pagination)
```bash
curl -X GET 'http://localhost:8080/teas?paginated=false'
```

#### List teas with custom pagination
```bash
curl -X GET 'http://localhost:8080/teas?page=0&size=10'
```

#### Filter teas by category
```bash
curl -X GET 'http://localhost:8080/teas?category=GREEN&paginated=false'
```
Available categories: `BLACK`, `GREEN`, `HERBAL`, `OOLONG`, `WHITE`, `FLORAL`, `OTHER`

#### Filter teas by caffeine level and origin
```bash
curl -X GET 'http://localhost:8080/teas?caffeineLevel=MEDIUM&origin=japan&paginated=false'
```
Available caffeine levels: `NONE`, `LOW`, `MEDIUM`, `HIGH`

#### Get tea by ID
```bash
curl -X GET http://localhost:8080/teas/{id}
```

#### Create new tea
```bash
curl -X POST http://localhost:8080/teas \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sencha",
    "origin": "japan",
    "description": "Japanese green tea",
    "ingredients": [
      {
        "name": "Green Tea Leaves",
        "quantity": 5.0,
        "unitOfMeasure": "GRAMS"
      }
    ],
    "category": "GREEN",
    "caffeineLevel": "MEDIUM"
  }'
```

#### Update tea
```bash
curl -X PUT http://localhost:8080/teas/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sencha Premium",
    "origin": "japan",
    "description": "Premium Japanese green tea",
    "ingredients": [
      {
        "name": "Premium Green Tea Leaves",
        "quantity": 6.0,
        "unitOfMeasure": "GRAMS"
      }
    ],
    "category": "GREEN",
    "caffeineLevel": "LOW"
  }'
```

#### Delete tea by ID
```bash
curl -X DELETE http://localhost:8080/teas/{id}
```

#### Delete teas with filters
```bash
curl -X DELETE 'http://localhost:8080/teas?category=GREEN'
```

---

### 🍿 Snacks

#### List all snacks (paginated - default)
```bash
curl -X GET http://localhost:8080/snacks
```

#### List all snacks (without pagination)
```bash
curl -X GET 'http://localhost:8080/snacks?paginated=false'
```

#### List snacks with custom pagination
```bash
curl -X GET 'http://localhost:8080/snacks?page=0&size=10'
```

#### Filter vegan snacks
```bash
curl -X GET 'http://localhost:8080/snacks?vegan=true&paginated=false'
```

#### Filter snacks by flavor
```bash
curl -X GET 'http://localhost:8080/snacks?flavour=cheese&paginated=false'
```

#### Filter snacks by sauce flavor
```bash
curl -X GET 'http://localhost:8080/snacks?sauce=spicy&paginated=false'
```

#### Get snack by ID
```bash
curl -X GET http://localhost:8080/snacks/{id}
```

#### Create new snack
```bash
curl -X POST http://localhost:8080/snacks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Chips",
    "flavour": "Salt",
    "vegan": true
  }'
```

#### Update snack
```bash
curl -X PUT http://localhost:8080/snacks/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Chips Updated",
    "flavour": "BBQ",
    "vegan": false
  }'
```

#### Delete snack by ID
```bash
curl -X DELETE http://localhost:8080/snacks/{id}
```

#### Delete all snacks
```bash
curl -X DELETE http://localhost:8080/snacks
```

#### List sauces of a snack
```bash
curl -X GET http://localhost:8080/snacks/{id}/sauces
```

#### Add sauce to a snack
```bash
curl -X POST http://localhost:8080/snacks/{snackId}/sauces/{sauceId}
```

#### Remove sauce from a snack
```bash
curl -X DELETE http://localhost:8080/snacks/{snackId}/sauces/{sauceId}
```

---

### 🌶️ Sauces

#### List all sauces (paginated - default)
```bash
curl -X GET http://localhost:8080/sauces
```

#### List all sauces (without pagination)
```bash
curl -X GET 'http://localhost:8080/sauces?paginated=false'
```

#### List sauces with custom pagination
```bash
curl -X GET 'http://localhost:8080/sauces?page=0&size=10'
```

#### Filter sauces by flavor
```bash
curl -X GET 'http://localhost:8080/sauces?flavour=spicy&paginated=false'
```

#### Get sauce by ID
```bash
curl -X GET http://localhost:8080/sauces/{id}
```

#### Create new sauce
```bash
curl -X POST http://localhost:8080/sauces \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hot Sauce",
    "flavour": "Spicy"
  }'
```

#### Update sauce
```bash
curl -X PUT http://localhost:8080/sauces/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Super Hot Sauce",
    "flavour": "Extra Spicy"
  }'
```

#### Delete sauce by ID
```bash
curl -X DELETE http://localhost:8080/sauces/{id}
```

#### Delete all sauces
```bash
curl -X DELETE http://localhost:8080/sauces
```

---

### 🔑 Authentication

#### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }'
```

Response:
```json
{
  "accessToken": "YOUR_JWT_TOKEN",
  "expiresIn": 3600,
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

#### Refresh Token
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

Response:
```json
{
  "accessToken": "YOUR_NEW_JWT_TOKEN",
  "expiresIn": 3600,
  "refreshToken": "YOUR_NEW_REFRESH_TOKEN"
}
```

#### Logout
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 👤 Users

#### Get Current User Info
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**⚠️ Requires ADMIN role**

Response:
```json
{
  "username": "admin",
  "email": "admin@maddoxbar.com",
  "roles": ["USER", "ADMIN"]
}
```

#### List All Users
```bash
# List only active users (default)
curl -X GET http://localhost:8080/auth/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# List all users (active and inactive)
curl -X GET 'http://localhost:8080/auth/users?active=' \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# List only inactive users
curl -X GET 'http://localhost:8080/auth/users?active=false' \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**⚠️ Requires ADMIN role**

Response:
```json
[
  {
    "username": "admin",
    "email": "admin@maddoxbar.com",
    "roles": ["USER", "ADMIN"]
  },
  {
    "username": "user",
    "email": "user@maddoxbar.com",
    "roles": ["USER"]
  }
]
```

---

## Packaging and running the application

can be packaged using:

```shell script
./gradlew build
```

produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

then runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

if you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

native executable:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

or run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

and, yes. it has swagger-ui too: `http://localhost:8080/swagger-ui`

also, ill try to keep this updated. :D

---

## 🚀 Próximos Passos

### **1. 🔧 Melhorias na Autenticação**
- [ ] **Refresh Token**: Implementar tokens de renovação
- [ ] **Email Verification**: Enviar email de confirmação após registro (maybe)
- [ ] **Password Reset**: Endpoint para recuperação de senha via email
- [ ] **Rate Limiting**: Limitar tentativas de login para prevenir ataques de força bruta
- [ ] **Audit Log**: Registrar ações importantes (login, logout, alterações críticas)

### **2. 🎯 Gestão de Permissões (RBAC)**
- [ ] Adicionar `@RolesAllowed("ADMIN")` nos endpoints de DELETE (teas, snacks, sauces)
- [ ] Criar role `MANAGER` para operações intermediárias
- [ ] Endpoint para admin gerenciar roles de outros usuários
- [ ] Endpoint para admin reativar contas desativadas
- [ ] Endpoint para admin desativar usuários (soft delete)

### **3. 📊 Relatórios e Analytics**
- [ ] Endpoint para estatísticas (total de chás por categoria, snacks veganos, etc.)
- [ ] Dashboard endpoint com métricas gerais
- [ ] Endpoint de produtos mais populares/acessados

### **4. 🔍 Melhorias de Busca**
- [ ] **Full-text search** nos produtos (buscar por nome, descrição, ingredientes)
- [ ] **Autocomplete** para busca rápida

### **5. 🖼️ Upload de Imagens**
- [ ] Adicionar campo `imageUrl` nas entidades
- [ ] Endpoint para upload de imagens de produtos
- [ ] Integração com storage (S3, MinIO, ou local)

### **6. 🧪 Testes**
- [ ] **Testes de Integração** para autenticação JWT
- [ ] Testes para os novos endpoints de usuários
- [ ] Testes de permissões (verificar 403 Forbidden)
- [ ] Testes de segurança (tentativas de acesso não autorizado)

### **7. 🐳 DevOps**
- [ ] **CI/CD** com GitHub Actions
- [ ] **Docker** multi-stage build
- [ ] **Kubernetes** manifests para deploy
- [ ] **Monitoring** com Prometheus/Grafana
- [ ] **Logging** centralizado

### **8. 📱 API Evolution**
- [ ] Versionamento de API (v1, v2)
- [ ] Editar constantes swagger novos endpoints

---

