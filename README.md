# SGP-Web — Gestão de Pedidos e Controle de Estoque para Confeitarias

Aplicação web para gerenciar insumos, fichas técnicas de produtos e pedidos. Ao confirmar um pedido, o
estoque de todos os insumos é validado e baixado de uma só vez (ou nada é alterado).

## Stack

Java 17 · Spring Boot 4 (Web, Data JPA, Validation) · MySQL 8 · HTML5/CSS3/JavaScript + Bootstrap 5

## Configurar o banco

1. Copie `.env.example` para `.env` e troque as senhas (o `.env` não é versionado).
2. Suba o MySQL (porta 3307):
   ```
   docker compose up -d
   ```
   Isso cria os bancos `sgp` e `sgp_test`. O `schema.sql` é aplicado pela própria aplicação ao iniciar.

## Rodar

```
set -a; . ./.env; set +a
./mvnw spring-boot:run
```

Abra http://localhost:8080. Endpoints principais: `/api/insumos`, `/api/insumos/abaixo-do-minimo`,
`POST /api/pedidos`, `POST /api/pedidos/{id}/confirmar`.

Erros: pedido inexistente → 404; estoque insuficiente ou regra de negócio violada → 409.

## Testes

Os testes de integração usam o banco `sgp_test`, com o MySQL acima no ar:

```
set -a; . ./.env; set +a
./mvnw test
```

Cobrem: confirmação com baixa de estoque, estoque insuficiente sem alterações, soma da baixa entre itens,
alerta de estoque mínimo e bloqueio de exclusão de insumo vinculado a ficha técnica.
