# Testes de integração

Executados com `./mvnw test` contra MySQL 8.0.46 real (banco `sgp_test`), em 27/10/2026.
Resultado da execução: 5 testes, 0 falhas, 0 erros (BUILD SUCCESS).
Os valores da coluna "Obtido" são os que as asserções do teste verificaram no banco.

| Caso | Entrada | Esperado | Obtido (real) | Resultado |
|---|---|---|---|---|
| CT01 — Estoque suficiente confirma e reduz estoque | Farinha 1000 g (mín. 100); Bolo usa 200 g; pedido de 2 bolos; confirmar | Status CONFIRMADO; estoque de farinha 600 g | Status CONFIRMADO (também no banco); farinha 600 g | PASSOU |
| CT02 — Insumo insuficiente | Farinha 1000 g e Leite 50 g; Bolo usa 200 g de farinha e 100 g de leite; pedido de 1 bolo; confirmar | `EstoqueInsuficienteException` citando Leite; estoques e status inalterados | Exceção lançada com "Leite"; farinha 1000 g, leite 50 g; status ABERTO | PASSOU |
| CT03 — Vários itens com o mesmo insumo | Açúcar 1000 g; 10 brigadeiros (10 g cada) + 2 tortas (150 g cada); confirmar | Baixa somada: 10×10 + 2×150 = 400 g; estoque 600 g | Açúcar 600 g | PASSOU |
| CT04 — Insumo abaixo do mínimo é sinalizado | Ovos 30 (mín. 20), Farinha 1000 (mín. 100); omelete usa 12 ovos; confirmar | Antes: nenhum alerta. Depois: só "Ovos" sinalizado | Lista vazia antes; depois exatamente ["Ovos"], com `abaixoDoMinimo` verdadeiro (farinha falso) | PASSOU |
| CT05 — Excluir insumo vinculado a ficha técnica | Chocolate usado na ficha do Brownie; tentar excluir | `RegraDeNegocioException`; insumo continua existindo | Exceção lançada; insumo ainda existe | PASSOU |
