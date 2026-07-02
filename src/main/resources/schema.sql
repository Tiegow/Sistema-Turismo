CREATE DATABASE IF NOT EXISTS agencia_turismo;
USE agencia_turismo;

-- Tabelas Pessoas e Atores

CREATE TABLE IF NOT EXISTS PESSOA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    identificacao VARCHAR(20) UNIQUE NOT NULL, -- CPF ou CNPJ
    nome VARCHAR(45) NOT NULL,
    email VARCHAR(45),
    telefone VARCHAR(45)
);

CREATE TABLE IF NOT EXISTS COLABORADOR (
    id_pessoa INT PRIMARY KEY,
    data_contratacao DATETIME,
    pj TINYINT NOT NULL,
    FOREIGN KEY (id_pessoa) REFERENCES PESSOA(id)
);

CREATE TABLE IF NOT EXISTS TURISTA (
    id_pessoa INT PRIMARY KEY,
    forma_pagamento VARCHAR(20),
    FOREIGN KEY (id_pessoa) REFERENCES PESSOA(id)
);

CREATE TABLE IF NOT EXISTS ACOMPANHANTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(45) NOT NULL,
    telefone VARCHAR(15),
    id_turista INT NOT NULL,
    FOREIGN KEY (id_turista) REFERENCES TURISTA(id_pessoa)
);

CREATE TABLE IF NOT EXISTS GUIA (
    id_colaborador INT PRIMARY KEY,
    FOREIGN KEY (id_colaborador) REFERENCES COLABORADOR(id_pessoa)
);

CREATE TABLE IF NOT EXISTS MOTORISTA (
    id_colaborador INT PRIMARY KEY,
    numero_cnh INT NOT NULL,
    validade DATETIME,
    FOREIGN KEY (id_colaborador) REFERENCES COLABORADOR(id_pessoa)
);

CREATE TABLE IF NOT EXISTS IDIOMAS (
    id_guia INT,
    idioma VARCHAR(45),
    PRIMARY KEY (id_guia, idioma),
    FOREIGN KEY (id_guia) REFERENCES GUIA(id_colaborador)
);

CREATE TABLE IF NOT EXISTS CNH_CATEGORIAS (
    id_motorista INT,
    cnh_categoria VARCHAR(30),
    PRIMARY KEY (id_motorista, cnh_categoria),
    FOREIGN KEY (id_motorista) REFERENCES MOTORISTA(id_colaborador)
);

-- Tabelas de Logística e Produtos

CREATE TABLE IF NOT EXISTS ROTEIRO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(45),
    duracao VARCHAR(45),
    modalidade VARCHAR(45),
    descricao VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS ROTEIRO_PRECO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_roteiro INT NOT NULL,
    data_cadastro DATETIME NOT NULL,
    ativo TINYINT NOT NULL,
    preco INT,
    UNIQUE (id_roteiro, data_cadastro),
    FOREIGN KEY (id_roteiro) REFERENCES ROTEIRO(id)
);

CREATE TABLE IF NOT EXISTS PASSEIO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    preco INT,
    capacidade INT,
    data_hora DATETIME,
    id_roteiro INT NOT NULL,
    FOREIGN KEY (id_roteiro) REFERENCES ROTEIRO(id)
);

CREATE TABLE IF NOT EXISTS VEICULO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    placa VARCHAR(15) UNIQUE NOT NULL,
    modelo VARCHAR(45),
    capacidade INT
);

CREATE TABLE IF NOT EXISTS MANUTENCAO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(45),
    data_entrada DATETIME NOT NULL,
    data_saida DATETIME,
    motivo VARCHAR(80),
    custo INT,
    id_veiculo INT NOT NULL,
    FOREIGN KEY (id_veiculo) REFERENCES VEICULO(id)
);

-- Tabelas de Operações

CREATE TABLE IF NOT EXISTS RESERVA (
    id_passeio INT,
    id_turista INT,
    qtd_vagas INT,
    pagamento_efetuado TINYINT,
    valor_total INT,
    local_embarque VARCHAR(45),
    PRIMARY KEY (id_passeio, id_turista),
    FOREIGN KEY (id_passeio) REFERENCES PASSEIO(id),
    FOREIGN KEY (id_turista) REFERENCES TURISTA(id_pessoa)
);

CREATE TABLE IF NOT EXISTS COLABORADOR_ALOCADO (
    id_colaborador INT,
    id_passeio INT,
    PRIMARY KEY (id_colaborador, id_passeio),
    FOREIGN KEY (id_colaborador) REFERENCES COLABORADOR(id_pessoa),
    FOREIGN KEY (id_passeio) REFERENCES PASSEIO(id)
);

CREATE TABLE IF NOT EXISTS VEICULO_ALOCADO (
    id_passeio INT,
    id_veiculo INT,
    PRIMARY KEY (id_passeio, id_veiculo),
    FOREIGN KEY (id_passeio) REFERENCES PASSEIO(id),
    FOREIGN KEY (id_veiculo) REFERENCES VEICULO(id)
);
