/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import algorithms.Algorithms;
import base.MaterializedView;
import drivers.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class DefineView extends Algorithms {

    private String select;
    private String from;
    private String where;
    private String groupBy;
    private String orderBy;

    public ArrayList<MaterializedView> getWorkloadSelected(ArrayList<MaterializedView> capturedQueries) {
        for (int i = 0; i < capturedQueries.size(); i++) {
            MaterializedView current = (MaterializedView) capturedQueries.get(i);
            current.setHypoMaterializedView(this.getDdlCreateViewFromQuery(current));
            capturedQueries.set(i, current);
        }
        return capturedQueries;
    }

    public String getDdlCreateViewFromQuery(MaterializedView query) {
        this.gerateClauseSelectForDDLView(query);
        this.gerateClauseFromForDDLView(query);
        this.gerateClauseWhereForDDLView(query);
        this.gerateClauseGroupByForDDLView(query);
        this.gerateClauseOrderByForDDLView(query);
        return this.getDdlCreateViewComplete();
    }

    private String getDdlCreateViewComplete() {
//        System.out.println(this.select);
//        System.out.println(this.from);
//        System.out.println(this.where);
//        System.out.println(this.groupBy);
//        System.out.println(this.orderBy);
        return this.treatComma(this.select) + " "
                + treatComma(this.from) + " "
                + treatComma(this.where) + " "
                + treatComma(this.groupBy) + " "
                + treatComma(this.orderBy);
    }

    private String treatComma(String query) {
        query = query.trim();
        if (query.length() > 0 && query.charAt(query.length() - 1) == ',') {
            query = query.substring(0, query.length() - 1);
        }
        return query;
    }

    public void gerateClauseSelectForDDLView(MaterializedView query) {
        this.select = query.getClauseFromSql("select").trim().replace("top 100", "");
        String fields = ", ";
        if (!this.select.equals("select *")) {
//            for (Table table : query.getTablesQuery()) {
//                for (String field : table.getFields()) {
//                    if ((query.getSql().contains(field)) && (query.getSql().contains(table.getName())) && !select.contains(field)) {
//                        if (!fields.equals(", ")) {
//                            fields += ", ";
//                        }
//                        fields += field;
//                    }
//                }
//            }
            fields = this.getAllFieldsWhere(query);
        }
        this.select = query.getComents() + this.select + fields;
        this.groupBy = fields;
    }

    public String getAllFieldsWhere(MaterializedView query) {
        this.where = query.getClauseFromSql("where").trim();
        String fields = ", ";
        if (!this.where.isEmpty()) {
            for (Table table : query.getTablesQuery()) {
                for (String field : table.getFields()) {
                    if (query.containsField(this.where, field)) {
                        int end = this.where.indexOf(field) + field.length();
                        String temp = this.where.substring(0, end);
                        int ini = temp.lastIndexOf(" ");
                        if (!temp.substring(ini - 1, ini + 1).equals("=")) {
                            if (!fields.equals(", ")) {
                                fields += ", ";
                            }
                            fields += this.where.substring(ini, end);
                        }
                    }
                }
            }
        }
        return fields;
    }

    public void gerateClauseFromForDDLView(MaterializedView query) {
        this.from = query.getClauseFromSql("from");
        for (Table table : query.getTablesQuery()) {
            if (!this.from.contains(table.getName())) {
                this.from += ", " + table.getName();
            }
        }
    }

    public void gerateClauseGroupByForDDLView(MaterializedView query) {
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

    public void gerateClauseOrderByForDDLView(MaterializedView query) {
        this.orderBy = query.getClauseFromSql("order by");
    }

    public boolean hasForceClauseGroupBy() {
        return !this.groupBy.trim().isEmpty() && !this.groupBy.trim().equals(",") && (this.select.contains("sum(") || this.select.contains("count("));
    }

    public void gerateClauseWhereForDDLView(MaterializedView query) {
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

    private boolean isConstrainValid(String constrain) {
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

    private boolean containNumber(String word) {
        if (word.contains("0")
                || word.contains("1")
                || word.contains("2")
                || word.contains("3")
                || word.contains("4")
                || word.contains("5")
                || word.contains("6")
                || word.contains("7")
                || word.contains("8")
                || word.contains("9")) {
            return true;
        }
        return false;
    }

}
