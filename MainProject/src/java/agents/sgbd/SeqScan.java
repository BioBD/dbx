/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

import java.util.ArrayList;

/**
 *
 * @author josemariamonteiro
 */
public class SeqScan {

    private String tableName;
    private ArrayList<Filter> filterColumns;
    private long cost;
    private long numberOfRows;

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    public SeqScan(String tableName, ArrayList<Filter> filterColumns) {
        this.tableName = tableName;
        this.filterColumns = filterColumns;
    }

    public SeqScan(String tableName, ArrayList<Filter> filterColumns, long cost) {
        this.tableName = tableName;
        this.filterColumns = filterColumns;
        this.cost = cost;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the filterColumns
     */
    public ArrayList<Filter> getFilterColumns() {
        return filterColumns;
    }

    /**
     * @return the cost
     */
    public long getCost() {
        return cost;
    }

    /**
     * @return the numberOfRows
     */
    public long getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @param numberOfRows the numberOfRows to set
     */
    public void setNumberOfRows(long numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

}
