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

import java.util.* ;

/**
 * ZQuery: an SQL SELECT statement
 */
public class ZQuery implements ZStatement, ZExp {
    
    Vector select_;
    boolean distinct_ = false;
    String into_ = null;
    Vector from_;
    ZExp where_ = null;
    ZGroupBy groupby_ = null;
    ZExpression setclause_ = null;
    Vector orderby_ = null;
    boolean forupdate_ = false;
    String alias_ = null;
    Vector aliasTableColumns_;
    ZJoin join_ = null;
    String limitValue1_ = null;
    String limitValue2_ = null;
    String offset_ = null;

    
    /**
     * Create a new SELECT statement
     */
    public ZQuery() {}

    public Vector getAliasTableColumns() {
        return aliasTableColumns_;
    }

    public String getLimitValue1() {
        return limitValue1_;
    }

    public void setLimitValue1(String limitValue1) {
        this.limitValue1_ = limitValue1;
    }

    public String getLimitValue2() {
        return limitValue2_;
    }

    public void setLimitValue2(String limitValue2) {
        this.limitValue2_ = limitValue2;
    }

    public void setAliasTableColumns(Vector columnsList) {
        this.aliasTableColumns_ = columnsList;
    }

    public String getOffset() {
        return offset_;
    }

    public void setOffset(String offset) {
        this.offset_ = offset;
    }

    public ZJoin getJoin() {
        return join_;
    }

    public void setJoin(ZJoin join) {
        this.join_ = join;
    }
    
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
    
    /**
     * Retorna a tabela temporária criada.
     * @return
     * Uma string.
     */
    public String getInto() { return into_; }
    
    /**
     * Insert the SELECT part of the statement
     * @param s A vector of ZSelectItem objects
     */
    public void addSelect(Vector s) { select_ = s; }
    
    /**
     * Insert the INTO part of the statement
     * @param tableName
     * O nome da tabela do comando INTO.
     */
    public void setInto(String tableName) { into_ = tableName; }
    
    /**
     * Insert the FROM part of the statement
     * @param f a Vector of ZFromItem objects
     */
    public void addFrom(Vector f) { from_ = f; }
    
    /**
     * Insert a WHERE clause
     * @param w An SQL Expression
     */
    public void addWhere(ZExp w) { where_ = w; }
    
    /**
     * Insert a GROUP BY...HAVING clause
     * @param g A GROUP BY...HAVING clause
     */
    public void addGroupBy(ZGroupBy g) { groupby_ = g; }
    
    /**
     * Insert a SET clause (generally UNION, INTERSECT or MINUS)
     * @param s An SQL Expression (generally UNION, INTERSECT or MINUS)
     */
    public void addSet(ZExpression s) { setclause_ = s; }
    
    /**
     * Insert an ORDER BY clause
     * @param v A vector of ZOrderBy objects
     */
    public void addOrderBy(Vector v) { orderby_ = v; }
    
    /**
     * Get the SELECT part of the statement
     * @return A vector of ZSelectItem objects
     */
    public Vector getSelect() { return select_; }
    
    
    
    /**
     * Remove the INTO part of the statement setting null value.
     */
    public void removeInto() { into_ = null; }
    
    /**
     * Get the FROM part of the statement
     * @return A vector of ZFromItem objects
     */
    public Vector getFrom() { return from_; }
    
    /**
     * Get the WHERE part of the statement
     * @return An SQL Expression or sub-query (ZExpression or ZQuery object)
     */
    public ZExp getWhere() { return where_; }
    
    /**
     * Get the GROUP BY...HAVING part of the statement
     * @return A GROUP BY...HAVING clause
     */
    public ZGroupBy getGroupBy() { return groupby_; }
    
    /**
     * Get the SET clause (generally UNION, INTERSECT or MINUS)
     * @return An SQL Expression (generally UNION, INTERSECT or MINUS)
     */
    public ZExpression getSet() { return setclause_; }
    
    /**
     * Get the ORDER BY clause
     * @param v A vector of ZOrderBy objects
     */
    public Vector getOrderBy() { return orderby_; }

    public void setDistinct(boolean distinct) {
        this.distinct_ = distinct;
    }
    
    /**
     * @return true if it is a SELECT DISTINCT query, false otherwise.
     */
    public boolean isDistinct() { return distinct_; }
    
    /**
     * @return true if it is a FOR UPDATE query, false otherwise.
     */
    public boolean isForUpdate() { return forupdate_; }
    
    
    private String printJoin(ZJoin join){
        //join.firstTable.elementAt(0);
        StringBuilder buf = new StringBuilder(printFrom(join.firstTable));
        buf.append(" ").append(join.joinType);
        buf.append(" JOIN");
        if(join.nestedJoin == null)
            buf.append(" ").append(printFrom(join.secondTable));
        else
            buf.append("(").append(printJoin(join.nestedJoin)).append(")");
        buf.append(" ON").append(join.OnExpression);
        
        return buf.toString();
    }
    
    private String printFrom(Vector from){
        StringBuilder buf = new StringBuilder();
        Object element = from.elementAt(0);
        if(element instanceof ZJoin)
            buf.append(printJoin((ZJoin)element));
        else
            if(element instanceof ZQuery){
                ZQuery query = (ZQuery)element;
                buf.append("(").append(query.toString()).append(")");
                if(query.alias_ != null){
                    buf.append(" AS ").append(query.alias_);
                    if(query.aliasTableColumns_ != null && query.aliasTableColumns_.size() > 0) {
                        buf.append("(" + query.aliasTableColumns_.elementAt(0));
                        for(int i=1; i<query.aliasTableColumns_.size(); i++)
                            buf.append("," + query.aliasTableColumns_.elementAt(i));
                        buf.append(")");
                    }
                }
            }else
                buf.append(element.toString());
        for(int i=1; i<from.size(); i++) {
            element = from.elementAt(i);
            if(element instanceof ZJoin)
                buf.append(printJoin((ZJoin)element));
            else
                if(element instanceof ZQuery){
                    ZQuery query = (ZQuery)element;
                    buf.append(",(").append(query.toString()).append(")");
                    if(query.alias_ != null)
                        buf.append(" AS ").append(query.alias_);
                }else
                    buf.append(", ").append(element.toString());
        }
        return buf.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("SELECT ");
        if(distinct_) buf.append("DISTINCT ");
        
        //buf.append(select_.toString());
        buf.append(select_.elementAt(0));
        for(int i=1; i<select_.size(); i++) {
            buf.append(", " + select_.elementAt(i));
        }
        
        if(into_ != null) {
            buf.append(" INTO " + into_.toString());
        }
        
        buf.append(" FROM ");
        if(join_ != null)
            buf.append(printJoin(join_));
        else{
         
         
            buf.append(printFrom(from_));
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
            for(int i=1; i<orderby_.size(); i++) {
                buf.append(", " + orderby_.elementAt(i).toString());
            }
        }
        
        if(limitValue1_ != null) buf.append(" LIMIT " + limitValue1_);
        
        if(limitValue2_ != null) buf.append(", " + limitValue2_);
        
        if(offset_ != null) buf.append(" OFFCET " + offset_);
            
        if(forupdate_) buf.append(" FOR UPDATE");
        
        return buf.toString();
    }

};

