/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.postgresql;

import base.Queries;

public class QueriesPostgreSQL extends Queries {

    @Override
    public String getSqlTableNames(String database) {
        return this.getSignatureToDifferentiate() + " select tablename,\n"
                + "array_to_string(array_agg(columns.name), ', ') AS fields\n"
                + "from \n"
                + "(SELECT tablename FROM pg_catalog.pg_tables \n"
                + "WHERE schemaname \n"
                + "NOT IN ('pg_catalog', 'information_schema', 'pg_toast') \n"
                + "ORDER BY schemaname, tablename) AS consulta\n"
                + "join \n"
                + "(\n"
                + "SELECT DISTINCT\n"
                + "    a.attname as name,\n"
                + "    pgc.relname\n"
                + "FROM pg_attribute a \n"
                + "JOIN pg_class pgc ON pgc.oid = a.attrelid\n"
                + "WHERE a.attnum > 0 AND pgc.oid = a.attrelid\n"
                + "AND pg_table_is_visible(pgc.oid)\n"
                + "AND NOT a.attisdropped\n"
                + ") as columns on (consulta.tablename = columns.relname)\n"
                + "group by \n"
                + "tablename";
    }

    @Override
    public String getSqlTableLength() {
        return this.getSignatureToDifferentiate() + " SELECT reltuples FROM pg_class WHERE relname= ?;";
    }

    @Override
    public String getSqlClauseToCreateMV(String query, String nameView) {
        return this.getSignatureToDifferentiate() + " DROP MATERIALIZED VIEW IF EXISTS public."
                + nameView + "; CREATE MATERIALIZED VIEW public." + nameView + " AS " + query + ";";
    }

    @Override
    public String getSqlClauseToCaptureCurrentQueries(String database) {
        return this.getSignatureToDifferentiate() + " select pid as processo_id, query as sql, query_start as inicio, datname as database_name from pg_stat_activity where datname like '" + database + "';";
    }

}
