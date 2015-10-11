create or replace function clearMaterializedView() returns integer
as
$body$
declare
  result integer;
  query text;
begin
  SELECT 'DROP MATERIALIZED VIEW ' || string_agg(oid::regclass::text, ', ') FROM   pg_class WHERE  relkind = 'm' into query;
  execute query;
  return 1;
end;
$body$
language plpgsql



create or replace function clearAllIndex() returns integer
as
$body$
declare
  result integer;
  query text;
begin
SELECT 
  'DROP INDEX ' || string_agg(nspname || '.' || relname, ', ')
 FROM pg_class, pg_index, pg_namespace
 WHERE pg_class.oid = pg_index.indexrelid
 and pg_namespace.oid = pg_class.relnamespace and nspname='public'

  AND pg_class.oid IN (
     SELECT indexrelid
     FROM pg_index, pg_class
     WHERE pg_class.oid=pg_index.indrelid
     AND relname !~ '^pg_') into query;
  execute query;
  return 1;
end;
$body$
language plpgsql



create or replace function clearIndexNotPrimary() returns integer
as
$body$
declare
  result integer;
  query text;
begin
SELECT 
  'DROP INDEX ' || string_agg(nspname || '.' || relname, ', ')
 FROM pg_class, pg_index, pg_namespace
 WHERE pg_class.oid = pg_index.indexrelid
 and pg_namespace.oid = pg_class.relnamespace
  AND pg_class.oid IN (
     SELECT indexrelid
     FROM pg_index, pg_class
     WHERE pg_class.oid=pg_index.indrelid
     AND indisprimary != 't'   
     AND relname !~ '^pg_') into query;

  execute query;
  return 1;
end;
$body$
language plpgsql





