# Dados do projeto SGP-Web

## Repositório
https://github.com/Cyteee/sgp-web-confeitaria

## Versões
| Item | Versão |
|---|---|
| Java | 17 (OpenJDK 17, definido em `pom.xml`) |
| Spring Boot | 4.1.2 |
| MySQL | 8.0.46 (contêiner Docker `mysql:8.0`) |
| Build | Maven 3.10.0 (Maven Wrapper `./mvnw`) |
| Bootstrap | 5.3.3 (CDN) |
| Sistema operacional | Ubuntu 25.04, kernel 6.14.0-37-generic |

## IDE e ferramentas
- IDE: [preencher]
- Docker 28.2 (MySQL em contêiner, porta 3307)
- Git e GitHub CLI (`gh`) para o repositório
- Playwright (Chrome headless, 1280x720) para as capturas de tela
- Spring Data JPA/Hibernate, Bean Validation, Lombok

## Endpoints REST
- `GET /api/insumos` — lista os insumos
- `GET /api/insumos/abaixo-do-minimo` — lista insumos com estoque igual ou abaixo do mínimo
- `POST /api/insumos` — cadastra um insumo
- `DELETE /api/insumos/{id}` — exclui um insumo (409 se estiver em ficha técnica)
- `GET /api/produtos` — lista produtos com a ficha técnica
- `GET /api/pedidos` — lista os pedidos
- `POST /api/pedidos` — registra um pedido (status ABERTO)
- `GET /api/pedidos/{id}` — consulta um pedido (404 se não existir)
- `POST /api/pedidos/{id}/confirmar` — confirma o pedido e dá baixa no estoque (409 se faltar insumo)
