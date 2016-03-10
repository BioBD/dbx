package iqt;

/**
 *
 * @author Arlino
 */
public class HeuristicsForSql {
    private String sql;
    private HeuristicsSelected heuristicsSelected;

    public HeuristicsForSql(String sql, HeuristicsSelected heuristicsSelected) {
        this.sql = sql;
        this.heuristicsSelected = heuristicsSelected;
    }
    
    public HeuristicsSelected getHeuristicsSelected() {
        return heuristicsSelected;
    }

    public void setHeuristicsSelected(HeuristicsSelected heuristicsSelected) {
        this.heuristicsSelected = heuristicsSelected;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}