CREATE TABLE IF NOT EXISTS insumo (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    nome               VARCHAR(100)  NOT NULL,
    unidade            VARCHAR(10)   NOT NULL,
    quantidade_estoque DECIMAL(12,3) NOT NULL DEFAULT 0,
    estoque_minimo     DECIMAL(12,3) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS produto (
    id    BIGINT         NOT NULL AUTO_INCREMENT,
    nome  VARCHAR(100)   NOT NULL,
    preco DECIMAL(10,2)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ficha_tecnica (
    produto_id BIGINT        NOT NULL,
    insumo_id  BIGINT        NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    PRIMARY KEY (produto_id, insumo_id),
    CONSTRAINT fk_ficha_produto FOREIGN KEY (produto_id) REFERENCES produto (id),
    CONSTRAINT fk_ficha_insumo  FOREIGN KEY (insumo_id)  REFERENCES insumo (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedido (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_nome VARCHAR(100) NOT NULL,
    data_pedido  DATETIME     NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ABERTO',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS item_pedido (
    pedido_id  BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT    NOT NULL,
    PRIMARY KEY (pedido_id, produto_id),
    CONSTRAINT fk_item_pedido  FOREIGN KEY (pedido_id)  REFERENCES pedido (id),
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
) ENGINE=InnoDB;
