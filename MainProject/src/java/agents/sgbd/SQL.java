/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

import agents.libraries.Configuration;
import agents.libraries.Log;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
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
    private Date timeFirstCapture;
    private Plan plan;
    private String database;
    private String hypoMaterializedView;

    private final ArrayList<Table> tablesQuery;
    private ArrayList<Column> fieldsQuery;
    private ArrayList<Date> timesOfCapture;
    private Schema schemaDataBase;

    public final Properties config;
    public final Log log;

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

    public void setCaptureCount(long captureCount) {
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

    public String getHypoMaterializedView() {
        if (this.getType().equals("Q")) {
            return hypoMaterializedView;
        } else {
            return "";
        }
    }

    public void setHypoMaterializedView(String hypoMaterializedView) {
        this.hypoMaterializedView = hypoMaterializedView;
    }

    public SQL() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
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
        } else if (this.getSql().toLowerCase().contains("delete")) {
            return "D";
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

    public Date getTimeFirstCapture() {
        return timeFirstCapture;
    }

    public void setTimeFirstCapture(String timeFirstCapture) {
        if (config.getProperty("sgbd").equals("oracle")) {
            log.error("implementar formato para oracle?");
        }
        if (timeFirstCapture != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(format.parse(timeFirstCapture).getTime());
                this.setTimeFirstCapture(date);
            } catch (ParseException ex) {
            }
        } else {
            this.setTimeFirstCapture(new Date());
        }

    }

    public void setTimeFirstCapture(Date timeFirstCapture) {
        this.timeFirstCapture = timeFirstCapture;
        this.addTimesOfCaptureNow();
    }

    public String getSql() {
        return sql;
    }

    protected String removerNl(String frase) {
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
        return this.plan.getPlan().trim();
    }

    public Plan plan() {
        return this.plan;
    }

    public boolean containsFieldOrTable(String clause, String field) {
        clause = clause.toLowerCase();
        field = field.toLowerCase();
        if (clause.contains(field)) {
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
        } else {
            return false;
        }
    }

    /* Devolver um ArrayList de Tabela, onde cada objeto Tabela tem um conjunto de atributos usados em uma ou mais cláusulas SELECT */
    public ArrayList<Table> getFieldsSelect(SQL query) {
        ArrayList<Table> tbSelect = new ArrayList();
        int count = 0, begin = 0, end = 0, inc = 0;

        sql = (query.getSql()).toLowerCase();

        if (sql.matches("select(.*)")) {
            Matcher m = Pattern.compile("([(]select)", Pattern.DOTALL).matcher(sql);
            while (m.find()) {
                count++;
            }

            /* Processa todas as sub consultas */
            for (int j = 0; j < count; j++) {
                /* Procura o sub processo atual */
                for (int i = 0; i < sql.length(); i++) {
                    if (sql.charAt(i) == '(') {
                        String select;
                        select = sql.substring(i + 1, i + 7);

                        if ("select".equals(select)) {
                            begin = i;
                        }
                        inc++;
                    } else if (sql.charAt(i) == ')') {
                        inc--;
                    }

                    if ((inc == 0) && (begin != 0)) {
                        end = i;
                        break;
                    }
                }

                /* Pega o sub processo da clausula */
                String sub = sql.substring(begin, end);

                /* Retira o sub processo da clausula */
                StringBuilder bSql = new StringBuilder(sql);
                bSql.delete(begin, end);
                sql = bSql.toString();

                /* Chama o método getFildsSubSelect e adiciona o retorno na tbSelect */
                tbSelect.addAll(getFildsSubSelect(sub));
            }

            /* Por fim, processa a consulta principal */
            tbSelect.addAll(getFildsSubSelect(sql));
        }
        return tbSelect;
    }

    /* Trata qualquer SELECT, até mesmo o principal e retorna uma lista de tabelas */
    public static ArrayList<Table> getFildsSubSelect(String subProcess) {
        ArrayList colSelect = new ArrayList();
        ArrayList<Table> tbList = new ArrayList();
        String strAtt = null, strTab = null, sql = null;
        String[] attArray = null, tabArray = null;

        Pattern rest;
        Matcher str;

        /* Obtendo o(s) nome(s) do(s) atributo(s) */
        rest = Pattern.compile("select(.*)from");
        str = rest.matcher(sql);
        while (str.find()) {
            strAtt = str.group();
        }

        /* Obtendo o(s) nome(s) da(s) tabela(s) */
        strTab = returnTableNames(sql, strAtt);
        strAtt = strAtt.substring(6, strAtt.length() - 4).trim();

        /* Organizando o(s) nome(s) do(s) atributo(s) em um array */
        if (strAtt.matches("(.*)[,](.*)")) {
            attArray = strAtt.split(",");
        } else {
            attArray = new String[1];
            attArray[0] = strAtt;
        }

        /* Organizando o(s) nome(s) da(s) tabela(s) em um array */
        if (strTab.matches("(.*)[,](.*)")) {
            tabArray = strTab.split(",");
        } else {
            tabArray = new String[1];
            tabArray[0] = strTab;
        }

        /* Procurando as tabelas e suas respectivas colunas */
        for (int i = 0; i < tabArray.length; i++) {
            Table tb = new Table();

            /* Método searchAttributes */
            tb = searchAttributes(tabArray, tabArray, i);

            /* Criando as tabelas a lista*/
            tbList.add(i, tb);

        }
        return tbList;
    }

    /* Retorna o(s) nome(s) da(s) tabela(s) */
    public static String returnTableNames(String mSql, String mAtt) {
        Pattern rest;
        Matcher str;
        String mTable = null;

        /* Obtendo o(s) nome(s) da(s) tabela(s) */
        if (mSql.matches("(.*)where(.*)")) {
            rest = Pattern.compile("select(.*)where");
            str = rest.matcher(mSql);
            while (str.find()) {
                mTable = str.group();
            }
            mTable = (mSql.substring(mAtt.length(), mTable.length() - 5)).trim();
        } else if (mSql.matches("(.*)group by(.*)")) {
            rest = Pattern.compile("select(.*)group by");
            str = rest.matcher(mSql);
            while (str.find()) {
                mTable = str.group();
            }
            mTable = (mSql.substring(mAtt.length(), mTable.length() - 8)).trim();
        } else if (mSql.matches("(.*)order by(.*)")) {
            rest = Pattern.compile("select(.*)order by");
            str = rest.matcher(mSql);
            while (str.find()) {
                mTable = str.group();
            }
            mTable = (mSql.substring(mAtt.length(), mTable.length() - 8)).trim();
        }

        return mTable;
    }

    public static Table searchAttributes(String[] mTb, String[] mAtt, int index) {
        Table table = new Table();
        String mSch = null, mStrTab = null, mStrAtt = null;
        ArrayList<Column> mCols = new ArrayList();
        Column mCol = new Column();
        Schema mSchema;

        Pattern rest;
        Matcher str;

        mStrTab = (mTb[index]).trim();
        /* Caso exista esquema */
        if (mStrTab.matches("(.*) (.*)")) {
            rest = Pattern.compile("(.*) ");
            str = rest.matcher(mStrTab);
            while (str.find()) {
                mStrTab = str.group();
            }
            mStrTab = (mStrTab).trim();

            mSch = (mTb[index].substring(mStrTab.length() + 1, mTb[index].length())).trim();

            for (int j = 0; j < mAtt.length; j++) {
                mStrAtt = (mAtt[j]).trim();
                if (mStrAtt.matches(mSch + "[.](.*)")) {
                    rest = Pattern.compile("[.](.*)");
                    str = rest.matcher(mStrAtt);
                    while (str.find()) {
                        mStrAtt = str.group();
                    }
                    mStrAtt = (mStrAtt).trim();
                    mStrAtt = mStrAtt.substring(1, mStrAtt.length());

                    /* Adicionar os atributos*/
                    mCol.setName(mStrAtt);
                    mCols.add(mCol);
                }
            }
        } /* Caso não exista esquema */ else {
            /* Analisa o esquema que está na memória */
            ArrayList<Table> mTables = new ArrayList<>();
            ArrayList<Column> mColumns = new ArrayList<>();
            mSchema = new Schema();
            mTables = mSchema.tables;

            for (int j = 0; j < mTables.size(); j++) {
                Table mTable = mTables.get(j);

                if (mStrTab == mTable.getName()) {
                    mColumns = mTable.getFields();

                    for (int k = 0; k < mAtt.length; k++) {

                        /* Verifica se o atributo do select não tem uma referencia */
                        if (!mAtt[k].matches("(.*)[.](.*)")) {

                            for (int l = 0; l < mColumns.size(); l++) {
                                Column mColumn = mColumns.get(l);
                                if (mAtt[k] == mColumn.getName()) {
                                    /* Adicionar os atributos*/
                                    mCol.setName(mAtt[k]);
                                    mCols.add(mCol);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        /* Montar TABELA table AQUI */
        table.setName(mStrTab);
        table.setFields(mCols);
        return table;
    }

    public ArrayList<Table> getFieldsGroup(SQL query) {
        ArrayList<Table> tbList = new ArrayList();
        String[] tabArray = null, attArray = null;
        String subStr = null, strTab = null, strAtt = null;

        String sql = ((query.getSql()).toLowerCase()).replaceAll(" ", "");

        if (sql.matches("select")) {
            Pattern rest;
            Matcher str;

            /* Obtendo o(s) nome(s) do(s) atributo(s) */
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);
            while (str.find()) {
                strAtt = str.group();
            }

            strTab = returnTableNames(sql, strAtt);

            /* Organizando o(s) nome(s) da(s) tabela(s) em um array */
            if (strTab.matches("(.*)[,](.*)")) {
                tabArray = strTab.split(",");
            } else {
                tabArray = new String[1];
                tabArray[0] = strTab;
            }

            //Verifica se há um ORDER BY no comando sql
            //Adiciona seus atributos em cols
            if (sql.matches("(.*)group by(.*)")) {

                rest = Pattern.compile("group by(.*)?(order by)");
                str = rest.matcher(sql);

                //Transforma a variável str em uma string chamada subStr
                while (str.find()) {
                    subStr = str.group();
                }

                StringBuilder bSql = null;
                bSql = new StringBuilder(subStr);
                /* Retira o ORDER BY */
                if (subStr.matches("(.*)order by(.*)")) {
                    bSql.delete(subStr.length() - 8, subStr.length());
                }
                /* Retira o GROUP BY */
                bSql.delete(0, 8);
                subStr = (bSql.toString()).trim();
                attArray = subStr.split(",");

                //Adiciona os atributos do ORDER BY no ArrayList cols
                for (int i = 0; i < attArray.length; i++) {
                    attArray[i] = (attArray[i]).trim();

                }
            }

            for (int i = 0; i < tabArray.length; i++) {
                Table tb = new Table();
                tb = searchAttributes(tabArray, tabArray, i);

                /* Criando as tabelas a lista*/
                tbList.add(i, tb);
            }
        }
        return tbList;
    }

    public ArrayList<Table> getFieldsOrder(SQL query) {
        ArrayList<Table> tbList = new ArrayList();
        String[] tabArray = null, attArray = null;
        String subStr = null, strTab = null, strAtt = null;
        Column col;

        String sql = (query.getSql()).toLowerCase();

        if (sql.matches("select(.*)")) {
            Pattern rest;
            Matcher str;

            /* Obtendo o(s) nome(s) do(s) atributo(s) */
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);
            while (str.find()) {
                strAtt = str.group();
            }

            strTab = returnTableNames(sql, strAtt);
            /* Organizando o(s) nome(s) da(s) tabela(s) em um array */
            if (strTab.matches("(.*)[,](.*)")) {
                tabArray = strTab.split(",");
            } else {
                tabArray = new String[1];
                tabArray[0] = strTab;
            }

            //Verifica se há um ORDER BY no comando sql
            //Adiciona seus atributos em cols
            if (sql.matches("(.*)order by(.*)")) {

                rest = Pattern.compile("order by(.*)");
                str = rest.matcher(sql);

                //Transforma a variável str em uma string chamada subStr
                while (str.find()) {
                    subStr = str.group();
                }

                StringBuilder bSql = null;
                bSql = new StringBuilder(subStr);
                /* Retira o GROUP BY */
                if (subStr.matches("(.*)group by(.*)")) {
                    bSql.delete(subStr.length() - 8, subStr.length());
                }
                /* Retira o ORDER BY */
                bSql.delete(0, 8);
                subStr = (bSql.toString()).trim();
                attArray = subStr.split(",");

                attArray = subStr.split("(asc,*|desc,*)");

                //Adiciona os atributos do ORDER BY no ArrayList cols
                for (int i = 0; i < attArray.length; i++) {
                    attArray[i] = (attArray[i]).trim();
                }
            }

            for (int i = 0; i < tabArray.length; i++) {
                Table tb = new Table();
                tb = searchAttributes(tabArray, tabArray, i);

                /* Criando as tabelas a lista*/
                tbList.add(i, tb);
            }
        }
        return tbList;
    }

    public void setResultSet(ResultSet resultset) {
        try {
            this.setId(resultset.getInt("wld_id"));
            this.setSql(resultset.getString("wld_sql").toLowerCase());
            this.setCaptureCount(resultset.getInt("wld_capture_count"));
            this.setAnalyzeCount(resultset.getInt("wld_analyze_count"));
            this.setRelevance(resultset.getInt("wld_relevance"));
            this.setPlan(resultset.getString("wld_plan").toLowerCase(), config.getProperty("sgbd"));
            if (resultset.getObject("cmv_ddl_create") != null) {
                this.setHypoMaterializedView(resultset.getString("cmv_ddl_create").toLowerCase());
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public float getDuration() {
        return this.plan.getDuration();
    }

}
