-- Dados de exemplo. IDs fixos + INSERT IGNORE: reiniciar a aplicação não duplica nem sobrescreve o estoque.
INSERT IGNORE INTO insumo (id, nome, unidade, quantidade_estoque, estoque_minimo) VALUES
    (1, 'Farinha de trigo', 'g',  5000, 1000),
    (2, 'Açúcar',           'g',  3000,  800),
    (3, 'Chocolate em pó',  'g',   400,  500),
    (4, 'Leite condensado', 'g',  2000,  600),
    (5, 'Ovos',             'un',   40,   12);

INSERT IGNORE INTO produto (id, nome, preco) VALUES
    (1, 'Bolo de chocolate', 55.00),
    (2, 'Brigadeiro (cento)', 80.00);

INSERT IGNORE INTO ficha_tecnica (produto_id, insumo_id, quantidade) VALUES
    (1, 1, 300), (1, 2, 200), (1, 3, 100), (1, 5, 3),
    (2, 4, 800), (2, 3, 200), (2, 2, 100);

INSERT IGNORE INTO pedido (id, cliente_nome, data_pedido, status) VALUES
    (1, 'Maria Silva', '2026-10-27 10:00:00', 'ABERTO');

INSERT IGNORE INTO item_pedido (pedido_id, produto_id, quantidade) VALUES
    (1, 1, 1);
