/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package bib.sgbd;

import static bib.base.Base.log;
import bib.sgbd.oracle.PlanOracle;
import bib.sgbd.postgresql.PlanPostgreSQL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rafael
 */
public class SQL {

    private int id;
    private String pid;
    private long captureCount;
    private long analyzeCount;
    private boolean waitAnalysis;
    private Integer relevance;
    private Date lastCapture;
    private String sql;
    private Time timeFirstCapture;
    private Plan plan;
    private String database;

    private final ArrayList<Table> tablesQuery;
    private ArrayList<Column> fieldsQuery;
    private ArrayList<Date> timesOfCapture;
    private Schema schemaDataBase;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getAnalyzeCount() {
        return analyzeCount;
    }

    public void setAnalyzeCount(long analyzeCount) {
        this.analyzeCount = analyzeCount;
    }

    public long getSizeRow() {
        return plan.getSizeRow();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public boolean isWaitAnalysis() {
        return waitAnalysis;
    }

    public void setWaitAnalysis(boolean waitAnalysis) {
        this.waitAnalysis = waitAnalysis;
    }

    public ArrayList<Date> getTimesOfCapture() {
        return timesOfCapture;
    }

    public void setTimesOfCapture(ArrayList<Date> timesOfCapture) {
        this.timesOfCapture = timesOfCapture;
    }

    public void addTimesOfCaptureNow() {
        Date now = new Date();
        this.timesOfCapture.add(now);
    }

    public long getCaptureCount() {
        if (captureCount > 0) {
            return captureCount;
        } else {
            return 1;
        }
    }

    public void setCaptureCount(Integer captureCount) {
        this.captureCount = captureCount;
    }

    public long getNumRow() {
        return this.plan.getNumRow();
    }

    public Date getLastCapture() {
        return lastCapture;
    }

    public void setLastCapture() {
        Date now = new Date();
        this.lastCapture = now;
    }

    public SQL() {
        super();
        this.tablesQuery = new ArrayList<>();
        this.fieldsQuery = new ArrayList<>();
        this.timesOfCapture = new ArrayList<>();
        this.setWaitAnalysis(true);
    }

    public long getCost() {
        return this.plan.getCost();
    }

    public Schema getSchemaDataBase() {
        return schemaDataBase;
    }

    public void setSchemaDataBase(Schema schemaDataBase) {
        this.schemaDataBase = schemaDataBase;
        this.setTablesQuery();
    }

    public ArrayList<Column> getFieldsQuery() {
        return fieldsQuery;
    }

    public void setFieldsQuery(ArrayList<Column> fieldsQuery) {
        this.fieldsQuery = fieldsQuery;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Table> getTablesQuery() {
        return tablesQuery;
    }

    public void setTablesQuery() {
        for (Table table : this.schemaDataBase.tables) {
            if (this.getSql() != null && !this.tablesQuery.contains(table) && this.containsFieldOrTable(this.getSql(), table.getName())) {
                table = this.setFieldsQuery(table);
                this.tablesQuery.add(table);
            }
        }
    }

    public Table setFieldsQuery(Table table) {
        ArrayList<Column> fields = table.getFields();
        for (Column field : fields) {
            if (this.containsFieldOrTable(this.getSql(), field.getName())) {
                this.fieldsQuery.add(field);
            }
        }
        return table;
    }

    public void setSql(String sql) {
        this.sql = this.removerNl(sql.trim());
        this.lastCapture = new Date();
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public String getType() {
        if (this.getSql().toLowerCase().contains("select")) {
            return "Q";
        } else if (this.getSql().toLowerCase().contains("update")) {
            return "U";
        } else if (this.getSql().toLowerCase().contains("insert")) {
            return "I";
        }
        return null;
    }

    public boolean hasTableInTableQuery(ArrayList<Table> tables) {
        for (Table tableCheck : tables) {
            if (this.tablesQuery.contains(tableCheck)) {
                return true;
            }
        }
        return false;
    }

    public String getComents() {
        if (this.getSql() != null) {
            int ini = this.getSql().indexOf("/*");
            if (ini >= 0) {
                int end = this.getSql().substring(ini).indexOf("*/") + ini + 2;
                return this.getSql().substring(ini, end).trim() + " ";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getClauseFromSql(String clause) {
        if (this.existClause(clause)) {
            int ini;
            if (clause.toLowerCase().contains("select") || clause.toLowerCase().contains("from") || clause.toLowerCase().contains("where")) {
                ini = this.getSql().toLowerCase().indexOf(clause) + clause.length();
            } else {
                ini = this.getSql().toLowerCase().lastIndexOf(clause) + clause.length();
            }
            int end = this.getEndClause(ini);
            String clauseComplete;
            if (end > 0) {
                clauseComplete = this.getSql().toLowerCase().substring(ini, end).trim();
            } else {
                clauseComplete = this.getSql().toLowerCase().substring(ini).trim();
            }
            return " " + clause + " " + clauseComplete;
        } else {
            return "";
        }
    }

    private int getEndClause(int ini) {
        int current = this.getSql().length();
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" from "));
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" where "));
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" group by "));
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" having "));
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" order by "));
        current = this.getSmaller(current, ini, this.getSql().toLowerCase().substring(ini).indexOf(" limit "));
        if (current > ini) {
            return current;
        } else {
            return 0;
        }
    }

    private int getSmaller(int current, int ini, int end) {
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

    public void incrementCaptureCount(int i) {
        this.captureCount += i;
        this.addTimesOfCaptureNow();

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.sql);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SQL other = (SQL) obj;
        if (!Objects.equals(this.sql, other.sql)) {
            return false;
        }
        return true;
    }

    public void setPlan(String plan, String sgbd) {
        if (!plan.isEmpty() && !sgbd.isEmpty()) {
            switch (sgbd) {
                case "postgresql":
                    this.plan = new PlanPostgreSQL(plan);
                    break;
                case "oracle":
                    this.plan = new PlanOracle(plan);
                    break;
            }
        }
    }

    public Time getTimeFirstCapture() {
        return timeFirstCapture;
    }

    public void setTimeFirstCapture(Time timeFirstCapture) {
        this.timeFirstCapture = timeFirstCapture;
        this.addTimesOfCaptureNow();
    }

    public String getSql() {
        return sql;
    }

    private String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        res = res.replaceAll("(\n|\r)+", " ");
        return res.trim();
    }

    public boolean isAlreadyCaptured() {
        return this.getCaptureCount() > 1;
    }

    public ArrayList<String> getNameClauses() {
        ArrayList<String> result = new ArrayList<>();
        result.add("select");
        result.add("from");
        result.add("where");
        result.add("group by");
        result.add("order by");
        result.add("having");
        result.add("limit");
        result.add("delete");
        result.add("update");
        result.add("set");
        result.add("insert");
        return result;
    }

    public Column getFielFromQuery(String fieldName) {
        for (Column column : this.fieldsQuery) {
            if (column.getName().toLowerCase().equals(fieldName.toLowerCase())) {
                return column;
            }
        }
        return null;
    }

    public void print() {
        log.msg("PID: " + id);
        log.msg("SQL: " + sql);
        log.msg("Start time: " + getTimeFirstCapture());
        log.msg("Type: " + this.getType());
        log.msg("Capture count: " + getCaptureCount());
        log.msg("Last Capture: " + getLastCapture());
        log.msg("Relevance: " + relevance);
        log.msg("Cost: " + this.getCost());
        log.msg("Tables: ");
        for (Table tablesQuery1 : tablesQuery) {
            log.msg("\t" + tablesQuery1.getName());
        }
        log.msg("Fields Query: ");
        for (Column fieldsQuery1 : fieldsQuery) {
            log.msg("\t" + fieldsQuery1.getName());
        }
    }

    public String getPlan() {
        return this.plan.getPlan();
    }

    public boolean containsFieldOrTable(String clause, String field) {
        clause = clause.toLowerCase();
        field = field.toLowerCase();
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
                || clause.contains("." + field + "<")
                || clause.substring(clause.length() - field.length()).equals(field);
    }

    public String getNameMaterizedView() {
        return "v_ot_workload_" + String.valueOf(this.getId());
    }

}
