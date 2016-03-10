/*
 * This file is part of Zql.
 *
 * Zql is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Zql is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zql.  If not, see <http://www.gnu.org/licenses/>.
 */

package br.com.iqt.zql;

import java.io.* ;
import java.util.* ;

/**
 * A name/alias association<br>
 * Names can have two forms:
 * <ul>
 * <li>FORM_TABLE for table names ([schema.]table)</li>
 * <li>FORM_COLUMN for column names ([[schema.]table.]column)</li>
 * <li>FORM_COLUMN_QUERY for column names (SELECT columns from tables)</li>
 * </ul>
 */
public class ZAliasedName implements java.io.Serializable {
    
    String strform_ = "";
    String schema_ = null;
    String table_ = null;
    String column_ = null;
    String alias_ = null;
    
    public static int FORM_TABLE = 1;
    public static int FORM_COLUMN = 2;
    public static int FORM_COLUMN_QUERY = 3;
    
    int form_ = FORM_COLUMN;
    
    public ZAliasedName() {}
    
    /**
     * Create a new ZAliasedName given it's full name.
     * @param fullname The full name: [[schema.]table.]column
     * @param form The name form (FORM_TABLE or FORM_COLUMN)
     */
    public ZAliasedName(String fullname, int form) {
        
        /*
         * Dois novos códigos inseridos:
         * 1 - Para resolver o problema da classe não reconhecer no parâmetro
         *     'fullname' possíveis agregações, e obtendo os nomes do esquema, tabela
         *     e coluna de forma errada.
         * 2 - Para resolver o problema do campo alias_ não ser setado na própria
         *     classe e sim em ZqlJJParser, fazendo com que o alias nunca seja capturado
         *     se ZAliasedName for usada sozinha.
         */
        
        //Início do código de 2
        if(alias_==null){
            StringTokenizer st2 = new StringTokenizer(fullname, " ");
            if(st2.countTokens() == 2){
                fullname = st2.nextToken();
                alias_ = st2.nextToken();
            }
        }
        //Fim do código de 2
        
        //Início do código de 1
        int begin = fullname.indexOf("(");
        int end = fullname.indexOf(")");
        if((begin!=-1)&&(end!=-1))
            fullname = fullname.substring(begin + 1, end);
        //Fim do código de 1
        
        form_ = form;
        strform_ = new String(fullname);
        
        StringTokenizer st = new StringTokenizer(fullname, ".");
        switch(st.countTokens()) {
            case 1:
                if(form == FORM_TABLE) table_ = new String(st.nextToken());
                else column_ = new String(st.nextToken());
                break;
            case 2:
                if(form == FORM_TABLE) {
                    schema_ = new String(st.nextToken());
                    table_ = new String(st.nextToken());
                } else {
                    table_ = new String(st.nextToken());
                    column_ = new String(st.nextToken());
                }
                break;
            case 3:
            default:
                schema_ = new String(st.nextToken());
                table_ = new String(st.nextToken());
                column_ = new String(st.nextToken());
                break;
        }
        /*
         * O alias (alias_) é setado na classeZqlJJParser e não em sua classe de origem
         * (ZAliasedName), impossibilitando sua captura quando apenas sua classe de origem
         * é utilizada. A inserssão do código abaixo resolve esse problema.
         */
        
        
    }
    
    public String toString() {
        if(form_ == ZAliasedName.FORM_COLUMN_QUERY)
            return "(" + strform_ + ")" + (alias_ == null ? "":" AS " + alias_);
        return strform_ + (alias_ == null ? "":" AS " + alias_);
    }
    
    /**
     * @return If the name is of the form schema.table.column,
     * returns the schema part
     */
    public String getSchema() { return schema_; }
    
    /**
     * @return If the name is of the form [schema.]table.column,
     * returns the schema part
     */
    public String getTable() { return table_; }
    
    /**
     * @return The name is of the form [[schema.]table.]column:
     * return the column part
     */
    public String getColumn() { return column_; }
    
    /**
     * @return true if column is "*", false otherwise.
     * Example: *, table.* are wildcards.
     */
    public boolean isWildcard() {
        if(form_ == FORM_TABLE) return table_ != null && table_.equals("*");
        else return column_ != null && column_.indexOf('*') >= 0;
    }
    
    /**
     * @return the alias associated to the current name.
     */
    public String getAlias() { return alias_; }
    
    /**
     * Associate an alias with the current name.
     * @param a the alias associated to the current name.
     */
    public void setAlias(String a) {
        alias_ = a;
        if((alias_ != null) && (alias_.isEmpty()))
            alias_ = null;
    }
}

