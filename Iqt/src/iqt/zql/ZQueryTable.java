/*
 * Classe implementada para suprir a necessidade de fazer uma consulta como tabela.
 * Por exemplo:
 * SELECT * FROM (SELECT * queyTable1)
 * 
 * arlinoh@yahoo.com.br
 */

package br.com.iqt.zql;

import java.util.* ;

/**
 * ZQueryTable: repesenta uma tabela em forma de consulta
 * Por exemplo:
 * SELECT * FROM (SELECT * queyTable1)
 */
/**
 *
 * @author Arlino Henrique
 */
public class ZQueryTable extends ZQuery{
    
    String alias_;
    
    /**
     * Create a new SELECT statement
     */
    public ZQueryTable() {}
    
    /**
     * Insere o alias da tabela.
     * @param aliasName
     * O nome do alias da tabela.
     */
    public void setAlias(String aliasName) { alias_ = aliasName; }
    
    /**
     * Retorna o alias da tabela.
     * @return 
     * Uma string.
     */
    public String getAlias() { return alias_; }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("SELECT ");
        if(distinct_) buf.append("DISTINCT ");
        
        //buf.append(select_.toString());
        int i;
        buf.append(select_.elementAt(0).toString());
        for(i=1; i<select_.size(); i++) {
            buf.append(", " + select_.elementAt(i).toString());
        }
        if(into_ != null) {
            buf.append(" INTO " + into_.toString());
        }
        buf.append(" FROM ");
        
        if(from_.elementAt(0) instanceof ZQueryTable){
            buf.append("(" + from_.elementAt(0).toString() + ")");
            if(alias_ != null)
                buf.append(" AS " + alias_);
        }
        else buf.append(from_.elementAt(0).toString());
        for(i=1; i<from_.size(); i++) {
            if(from_.elementAt(i) instanceof ZQueryTable){
                buf.append(", (" + from_.elementAt(i).toString() + ")");
                if(alias_ != null)
                    buf.append(" AS " + alias_);
            }
            else buf.append(", " + from_.elementAt(i).toString());
        }
        
        if(where_ != null)
            buf.append(" WHERE " + where_.toString());
        if(groupby_ != null) {
            buf.append(" " + groupby_.toString());
        }
        if(setclause_ != null) {
            buf.append(" " + setclause_.toString());
        }
        if(orderby_ != null) {
            buf.append(" ORDER BY ");
            //buf.append(orderby_.toString());
            buf.append(orderby_.elementAt(0).toString());
            for(i=1; i<orderby_.size(); i++) {
                buf.append(", " + orderby_.elementAt(i).toString());
            }
        }
        if(forupdate_) buf.append(" FOR UPDATE");
        
        return buf.toString();
    }

};

