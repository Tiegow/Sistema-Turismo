USE agencia_turismo;

-- Pessoas Iniciais (Os IDs serão gerados automaticamente: 1, 2, 3, 4)
INSERT INTO PESSOA (identificacao, nome, email, telefone) VALUES ('11122233344', 'João Gestor', 'joao@agencia.com', '11999999999');
INSERT INTO PESSOA (identificacao, nome, email, telefone) VALUES ('55566677788', 'Maria Guia', 'maria@agencia.com', '11888888888');
INSERT INTO PESSOA (identificacao, nome, email, telefone) VALUES ('99900011122', 'Carlos Motorista', 'carlos@agencia.com', '11777777777');
INSERT INTO PESSOA (identificacao, nome, email, telefone) VALUES ('33344455566', 'Ana Turista', 'ana@email.com', '11666666666');

-- Colaboradores (João id=1, Maria id=2, Carlos id=3)
INSERT INTO COLABORADOR (id_pessoa, data_contratacao, pj) VALUES (1, '2023-01-10 10:00:00', 0);
INSERT INTO COLABORADOR (id_pessoa, data_contratacao, pj) VALUES (2, '2023-02-15 09:00:00', 1);
INSERT INTO COLABORADOR (id_pessoa, data_contratacao, pj) VALUES (3, '2023-03-20 08:00:00', 0);

-- Turista (Ana id=4)
INSERT INTO TURISTA (id_pessoa, forma_pagamento) VALUES (4, 'PIX');

-- Guia (Maria id=2) e Motorista (Carlos id=3)
INSERT INTO GUIA (id_colaborador) VALUES (2);
INSERT INTO MOTORISTA (id_colaborador, numero_cnh, validade) VALUES (3, 123456789, '2028-12-31 23:59:59');

-- Idiomas e CNH
INSERT INTO IDIOMAS (id_guia, idioma) VALUES (2, 'Inglês');
INSERT INTO IDIOMAS (id_guia, idioma) VALUES (2, 'Espanhol');
INSERT INTO CNH_CATEGORIAS (id_motorista, cnh_categoria) VALUES (3, 'D');

-- Roteiro e Preço
INSERT INTO ROTEIRO (nome, duracao, modalidade, descricao) VALUES ('City Tour Histórico', '4 horas', 'Walking Tour', 'Passeio a pé pelos principais pontos históricos do centro da cidade.');
INSERT INTO ROTEIRO_PRECO (id_roteiro, data_cadastro, ativo, preco) VALUES (1, '2023-05-01 10:00:00', 1, 150);

-- Veículos (O ID será gerado automaticamente: 1)
INSERT INTO VEICULO (placa, modelo, capacidade) VALUES ('ABC1234', 'Van Mercedes Sprinter', 15);

-- Passeios
INSERT INTO PASSEIO (preco, capacidade, data_hora, id_roteiro) VALUES (150, 15, '2027-10-15 09:00:00', 1);

-- Reservas (Ana reserva 2 vagas)
INSERT INTO RESERVA (id_passeio, id_turista, qtd_vagas, pagamento_efetuado, valor_total, local_embarque) 
VALUES (1, 4, 2, 1, 300, 'Hotel Central');

-- Alocações (Usando os IDs criados: Maria=2, Carlos=3, Veiculo=1)
INSERT INTO COLABORADOR_ALOCADO (id_colaborador, id_passeio) VALUES (2, 1);
INSERT INTO COLABORADOR_ALOCADO (id_colaborador, id_passeio) VALUES (3, 1);
INSERT INTO VEICULO_ALOCADO (id_passeio, id_veiculo) VALUES (1, 1);
