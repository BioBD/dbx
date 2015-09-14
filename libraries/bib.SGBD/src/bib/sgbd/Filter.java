/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package bib.sgbd;

/**
 *
 * @author josemariamonteiro
 */
public class Filter extends Column{
    
   // private Column column;
    private String filterType;// theta ou equi
    
    /*
    public Filter(Column column, String filterType){
        this.column = column;
        this.filterType = filterType;
    }
    */

    /**
     * @return the column
     */
    /*
    public Column getColumn() {
        return column;
    }
    */

    /**
     * @param column the column to set
     */
    /*
    public void setColumn(Column column) {
        this.column = column;
    }
*/
    /**
     * @return the filterType
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * @param filterType the filterType to set
     */
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
    
    
}
