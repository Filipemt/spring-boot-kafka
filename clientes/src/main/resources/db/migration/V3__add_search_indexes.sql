create extension if not exists pg_trgm;

create index idx_clientes_nome_trgm on clientes using gin (lower(nome) gin_trgm_ops);
create index idx_clientes_email_trgm on clientes using gin (lower(email) gin_trgm_ops);
create index idx_clientes_ativo on clientes (ativo) where ativo = true;
