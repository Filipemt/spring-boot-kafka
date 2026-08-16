create table produtos (
    codigo         bigserial not null primary key,
    nome           varchar(100) not null,
    valor_unitario decimal(16, 2) not null,
    ativo          boolean not null default true
);
