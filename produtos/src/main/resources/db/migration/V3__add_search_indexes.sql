create extension if not exists pg_trgm;

create index idx_produtos_nome_trgm on produtos using gin (lower(nome) gin_trgm_ops);
create index idx_produtos_ativo on produtos (ativo) where ativo = true;
