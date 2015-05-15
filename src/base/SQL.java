/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import drivers.Schema;
import drivers.Table;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class SQL extends Base {

    private int id;
    private String sql;
    private String plan;
    private int capture_count;
    private int analyze_count;
    private int relevance;
    private String type;
    private ArrayList<Table> tablesQuery;
    private ArrayList<String> fieldsQuery;
    private Schema schemaDataBase;
    protected BigInteger cost;

    public BigInteger getCost() {
        return cost;
    }

    public Schema getSchemaDataBase() {
        return schemaDataBase;
    }

    public void setSchemaDataBase(Schema schemaDataBase) {
        this.schemaDataBase = schemaDataBase;
        this.setTablesQuery();
    }

    public ArrayList<String> getFieldsQuery() {
        return fieldsQuery;
    }

    public void setFieldsQuery(ArrayList<String> fieldsQuery) {
        this.fieldsQuery = fieldsQuery;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setResultSet(ResultSet resultset) {
        try {
            this.tablesQuery = new ArrayList<>();
            this.setId(resultset.getInt("wld_id"));
            this.setSql(resultset.getString("wld_sql").toLowerCase());
            this.setCapture_count(resultset.getInt("wld_capture_count"));
            this.setAnalyze_count(resultset.getInt("wld_analyze_count"));
            this.setRelevance(resultset.getInt("wld_relevance"));
            this.setType(resultset.getString("wld_type").toLowerCase());
            this.setPlan(resultset.getString("wld_plan").toLowerCase());
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public ArrayList<Table> getTablesQuery() {
        return tablesQuery;
    }

    public void setTablesQuery() {
        for (Table table : this.schemaDataBase.tables) {
            if (this.sql.indexOf(" " + table.getName() + " ") >= 0
                    || this.sql.indexOf("," + table.getName() + ",") >= 0
                    || this.sql.indexOf("," + table.getName() + " ") >= 0
                    || this.sql.indexOf(" " + table.getName() + ",") >= 0) {
                table = this.setFieldsQuery(table);
                this.tablesQuery.add(table);
            }
        }
    }

    public Table setFieldsQuery(Table table) {
        ArrayList<String> fields = table.getFields();
        ArrayList<String> result = new ArrayList<>();
        for (String field : fields) {
            if (this.sql.indexOf(field) >= 0) {
                result.add(table.getName() + "." + field);
            }
        }
        table.setFields(result);
        return table;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = this.removerNl(sql);
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
        this.setType();
    }

    public Integer getCapture_count() {
        return capture_count;
    }

    public void setCapture_count(Integer capture_count) {
        this.capture_count = capture_count;
    }

    public Integer getAnalyze_count() {
        return analyze_count;
    }

    public void setAnalyze_count(Integer analyze_count) {
        this.analyze_count = analyze_count;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType() {
        if (this.sql.contains("select")) {
            this.type = "Q";
        } else if (this.sql.contains("update")) {
            this.type = "U";
        }
    }

    public boolean haveTableInTableQuery(ArrayList<Table> tables) {
        for (Table tableCheck : tables) {
            if (this.tablesQuery.contains(tableCheck)) {
                return true;
            }
        }
        return false;
    }

    public String getNameMaterizedView() {
        if (this.getComentsForName().isEmpty()) {
            return "v_acm_workload_" + String.valueOf(this.getId());
        } else {
            return "v_acm_workload_" + this.getComentsForName();
        }
    }

    public String getComents() {
        int ini = this.sql.indexOf("/*");
        if (ini >= 0) {
            int end = this.sql.substring(ini).indexOf("*/") + ini + 2;
            return this.sql.substring(ini, end).trim() + " ";
        } else {
            return "";
        }
    }

    public String getComentsForName() {
        String name = this.getComents();
        name = name.replace("*", "");
        name = name.replace("/", "");
        name = name.replace(" ", "_");
        name = name.replace("-", "_");
        while (name.charAt(0) == '_') {
            name = name.substring(1, name.length());
        }
        while (name.charAt(name.length() - 1) == '_') {
            name = name.substring(0, name.length() - 2);
        }
        return name;
    }

    public String getClauseFromSql(String clause) {
        if (existClause(clause)) {
            int ini;
            if (clause.contains("select") || clause.contains("from") || clause.contains("where")) {
                ini = this.getSql().indexOf(clause) + clause.length();
            } else {
                ini = this.getSql().lastIndexOf(clause) + clause.length();
            }
            int end = this.getEndClause(ini);
            String clauseComplete;
            if (end > 0) {
                clauseComplete = this.getSql().substring(ini, end).trim();
            } else {
                clauseComplete = this.getSql().substring(ini).trim();
            }
            return " " + clause + " " + clauseComplete;
        } else {
            return "";
        }
    }

    public int getEndClause(int ini) {
        int current = this.sql.length();
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" from "));
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" where "));
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" group by "));
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" having "));
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" order by "));
        current = this.getSmaller(current, ini, this.getSql().substring(ini).indexOf(" limit "));
        if (current > ini) {
            return current;
        } else {
            return 0;
        }

    }

    public int getSmaller(int current, int ini, int end) {
        if (end < 0) {
            return current;
        }
        if ((ini + end) < current) {
            return (ini + end);
        } else {
            return current;
        }
    }

    public boolean existClause(String clause) {
        return this.getSql().toLowerCase().contains(clause);
    }

}
