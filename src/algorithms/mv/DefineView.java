/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import bib.base.Base;
import bib.sgbd.Column;
import bib.sgbd.SQL;
import bib.sgbd.Table;
import java.util.ArrayList;
import mv.MaterializedView;

/**
 *
 * @author Rafael
 */
public class DefineView extends Base {

    protected String select = "";
    protected String from = "";
    protected String where = "";
    protected String groupBy = "";
    protected String orderBy = "";
    protected ArrayList<String> fieldsWhere = new ArrayList<>();

    public ArrayList<MaterializedView> getWorkloadSelected(ArrayList<MaterializedView> capturedQueries) {
        for (int i = 0; i < capturedQueries.size(); i++) {
            MaterializedView current = (MaterializedView) capturedQueries.get(i);
            current.setHypoMaterializedView(this.getDdlCreateViewFromQuery(current));
            capturedQueries.set(i, current);
        }
        return capturedQueries;
    }

    protected String getDdlCreateViewFromQuery(MaterializedView query) {
        System.out.println(query.getSql());
        this.gerateClauseSelectForDDLView(query);
        this.gerateClauseFromForDDLView(query);
        this.gerateClauseWhereForDDLView(query);
        this.gerateClauseGroupByForDDLView(query);
        this.gerateClauseOrderByForDDLView(query);
        System.out.println(getDdlCreateViewComplete());
        return this.getDdlCreateViewComplete();
    }

    protected String getDdlCreateViewComplete() {
        return this.treatComma(this.select) + " "
                + treatComma(this.from) + " "
                + treatComma(this.where) + " "
                + treatComma(this.groupBy) + " "
                + treatComma(this.orderBy);
    }

    protected String treatComma(String query) {
        query = query.trim();
        if (query.length() > 0 && query.charAt(query.length() - 1) == ',') {
            query = query.substring(0, query.length() - 1);
        }
        return query;
    }

    protected void gerateClauseSelectForDDLView(SQL query) {
        this.select = query.getClauseFromSql("select");
        String fields = "";
        if (!this.select.equals("select *")) {
            for (Column fieldWhere : query.getFieldsQuery()) {
                fields += ", " + fieldWhere.getName();
            }
        }
        this.select = query.getComents() + "\n" + this.select + fields;
        this.groupBy = fields;
    }

    protected void gerateClauseFromForDDLView(SQL query) {
        this.from = query.getClauseFromSql("from");
        for (Table table : query.getTablesQuery()) {
            if (!this.from.contains(table.getName())) {
                this.from += ", " + table.getName();
            }
        }
    }

    protected void gerateClauseGroupByForDDLView(SQL query) {
        if (!this.groupBy.isEmpty() && query.existClause("group by")) {
            this.groupBy = query.getClauseFromSql("group by") + this.groupBy;
        } else {
            if (this.hasForceClauseGroupBy()) {
                this.groupBy = " group by " + this.groupBy.substring(1);
            } else {
                this.groupBy = "";
            }
        }
    }

    protected void gerateClauseOrderByForDDLView(MaterializedView query) {
        this.orderBy = query.getClauseFromSql("order by");
    }

    public boolean hasForceClauseGroupBy() {
        return !this.groupBy.trim().isEmpty() && !this.groupBy.trim().equals(",") && (this.select.contains("sum(") || this.select.contains("count("));
    }

    protected void gerateClauseWhereForDDLView(MaterializedView query) {
        String clause = query.getClauseFromSql("where");
        if (!clause.isEmpty()) {
            Combinacao combination = new Combinacao();
            ArrayList<String> lista = combination.dividirExpressaoPredicado(clause);
            this.where = "";
            for (String constrain : lista) {
                if (isConstrainValid(constrain) && !this.where.contains(constrain)) {
                    if (!this.where.isEmpty()) {
                        this.where += " and ";
                    }
                    this.where += " " + constrain;
                }
            }
            if (!this.where.isEmpty()) {
                this.where = "where " + this.where;
            }
        } else {
            this.where = "";
        }
    }

    protected boolean isConstrainValid(String constrain) {
        if (constrain.contains("'") || constrain.contains("\"")) {
            return false;
        }
        if (constrain.contains("<")) {
            return false;
        }
        if (constrain.contains(">")) {
            return false;
        }
        String[] words = constrain.split(" ");
        for (String word : words) {
            if (this.containNumber(word)) {
                return false;
            }
        }
        return true;
    }

    protected boolean containNumber(String word) {
        return word.contains("0")
                || word.contains("1")
                || word.contains("2")
                || word.contains("3")
                || word.contains("4")
                || word.contains("5")
                || word.contains("6")
                || word.contains("7")
                || word.contains("8")
                || word.contains("9");
    }

}
