package iqt.util;

import iqt.ConnectionDbms;
import iqt.Dbms;
import br.com.iqt.zql.ZFromItem;
import java.util.Vector;

/**
 *
 * @author Arlino
 */
public class DatabaseFunctions {
    
    /**
     * Verifica se uma coluna possui índice em de alguma das tabelas do vetor 
     * <b>tables</b>.
     * @param tables
     * Vetor de ZFromItem instanciado com tabela(s) onde irá ser verifado a presença
     * da chave-primária <b>column</b>.
     * @param column
     * String com o nome da coluna que será a existencia de índice em 
     * alguma das tabelas do vetor <b>tables</b>.
     * @return
     * Retorna <i>true</i> se econtrar  a chave-primária em alguma das tabelas e 
     * <i>false</i> calso contrário.
     */
    public static final boolean isColumnWithIndex(Dbms dbms, Vector tables, String column){
        for (Object object : tables) {
            ZFromItem fi = (ZFromItem) object;
            String table = fi.getTable();
            String schema = fi.getSchema();
            if(schema == null || schema.isEmpty())
                schema = "public";
            boolean result = ConnectionDbms.isIndex(dbms, schema, table, column);
            if(result)
                return true;
        }
        return false;
    }
    
    /**
     * Verifica se uma coluna é uma chave-primária de alguma das tabelas do vetor 
     * <b>tables</b>.
     * @param tables
     * Vetor de ZFromItem instanciado com tabela(s) onde irá ser verifado a presença
     * da chave-primária <b>column</b>.
     * @param column
     * String com o nome da coluna que será verificada como chave-primária de alguma 
     * das tabelas do vetor <b>tables</b>.
     * @return
     * Retorna <i>true</i> se econtrar  a chave-primária em alguma das tabelas e 
     * <i>false</i> calso contrário.
     */
    public static final boolean isColumnKey(Dbms dbms, Vector tables, String column){
        for (Object object : tables) {
            ZFromItem fi = (ZFromItem) object;
            String table = fi.getTable();
            boolean result = ConnectionDbms.isKey(dbms, table, column);
            if(result)
                return true;
        }
        return false;
    }
    
    /**
     * @param column
     * String com o nome da coluna que será verificada seu tipo.
     * @param tables
     * Vetor de ZFromItem instanciado com a(s) tabela(s) onde irá ser verifado a presença
     * da coluna <b>column</b>.
     * @return
     * Retorna o tipo da coluna <b>column</b>.
     */
    public static final String getType(String column, Vector tables){
        return "SMALLINT";
    }
}
