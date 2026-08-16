create table clientes (
    codigo      bigserial not null primary key,
    nome        varchar(150) not null,
    cpf         char(11) not null unique,
    logradouro  varchar(100),
    numero      varchar(10),
    bairro      varchar(100),
    email       varchar(150),
    telefone    varchar(20),
    ativo       boolean not null default true
);
