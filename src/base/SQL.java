/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import drivers.Schema;
import drivers.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
    public ArrayList<String> fieldsSelect;
    public ArrayList<String> fieldsWhere;

    private String where;

    private Schema schemaDataBase;
    protected long cost;
    private String select;

    public long getCost() {
        return cost;
    }

    public Schema getSchemaDataBase() {
        return schemaDataBase;
    }

    public void setSchemaDataBase(Schema schemaDataBase) {
        this.schemaDataBase = schemaDataBase;
        this.setTablesQuery();
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
            this.setSql(resultset.getString("wld_sql"));
            this.setCapture_count(resultset.getInt("wld_capture_count"));
            this.setAnalyze_count(resultset.getInt("wld_analyze_count"));
            this.setRelevance(resultset.getInt("wld_relevance"));
            this.setType(resultset.getString("wld_type"));
            this.setPlan(resultset.getString("wld_plan"));
            this.readAllFieldsWhere();
            this.readAllFieldsSelect();
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
        this.sql = sql;
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
        if (!name.isEmpty()) {
            while (name.charAt(0) == '_') {
                name = name.substring(1, name.length());
            }
            while (name.charAt(name.length() - 1) == '_') {
                name = name.substring(0, name.length() - 2);
            }
        }
        return name;
    }

    public String removeComents(String clause) {
        if (clause.contains("*/")) {
            return clause.substring(clause.lastIndexOf("*/") + 2).trim();
        } else {
            return clause.trim();
        }
    }

    public String getClauseFromSql(String clause) {
        if (existClause(clause)) {
            int ini;
            String clauseComplete;
            switch (clause) {
                case "select":
                case "from":
                case "where":
                    ini = this.getSqlCommLess().indexOf(clause) + clause.length();
                    break;
                default:
                    ini = this.getSqlCommLess().lastIndexOf(clause) + clause.length();
            }
            int end = this.getEndClause(ini);
            if (end > 0) {
                clauseComplete = this.getSqlCommLess().substring(ini, end).trim();
            } else {
                clauseComplete = this.getSqlCommLess().substring(ini).trim();
            }
            return (" " + clause + " " + clauseComplete).trim();
        } else {
            return "";
        }
    }

    public int getEndClause(int ini) {
        int current = this.getSqlCommLess().length();
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" from "));
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" where "));
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" group by "));
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" having "));
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" order by "));
        current = this.getSmaller(current, ini, this.getSqlCommLess().substring(ini).indexOf(" limit "));
        if (current > ini) {
            return current;
        } else {
            return 0;
        }
    }

    private String getSqlCommLess() {
        return this.removeComents(this.getSql());
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

    private void readAllFieldsWhere() {
        this.where = this.getClauseFromSql("where");
        this.fieldsWhere = new ArrayList<>();
        if (!this.where.isEmpty()) {
            for (Table table : this.getTablesQuery()) {
                for (String field : table.getFields()) {
                    if (this.containsField(this.where, field)) {
                        int end = this.where.indexOf(field) + field.length();
                        String temp = this.where.substring(0, end);
                        int ini = temp.lastIndexOf(" ");
                        if (!temp.substring(ini - 1, ini + 1).equals("=")) {
                            this.fieldsWhere.add(this.where.substring(ini, end));
                        }
                    }
                }
            }
        }
    }

    private void readAllFieldsSelect() {
        this.select = this.getClauseFromSql("select");
        this.fieldsSelect = new ArrayList<>();
        if (!this.select.isEmpty()) {
            this.fieldsSelect.addAll(Arrays.asList(this.select.replace("select ", "").split(",")));
        }
    }

    public boolean containsField(String clause, String field) {
        return clause.contains(" " + field + " ")
                || clause.contains(" " + field + ",")
                || clause.contains(" " + field + ";")
                || clause.contains(" " + field + "=")
                || clause.contains(" " + field + ">")
                || clause.contains(" " + field + "<")
                || clause.contains("," + field + ",")
                || clause.contains("," + field + ";")
                || clause.contains("," + field + "=")
                || clause.contains("," + field + ">")
                || clause.contains("," + field + "<")
                || clause.contains("." + field + ",")
                || clause.contains("." + field + ";")
                || clause.contains("." + field + "=")
                || clause.contains("." + field + ">")
                || clause.contains("." + field + "<");
    }
}
