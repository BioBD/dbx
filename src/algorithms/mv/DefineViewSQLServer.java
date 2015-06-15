/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.mv;

import base.MaterializedView;
import bib.sgbd.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class DefineViewSQLServer extends DefineView {

    @Override
    public ArrayList<MaterializedView> getWorkloadSelected(ArrayList<MaterializedView> capturedQueries) {
        for (int i = 0; i < capturedQueries.size(); i++) {
            MaterializedView current = (MaterializedView) capturedQueries.get(i);
            current.setHypoMaterializedView(this.getDdlCreateViewFromQuery(current));
            capturedQueries.set(i, current);
        }
        return capturedQueries;
    }

    @Override
    protected String getDdlCreateViewFromQuery(MaterializedView query) {
        this.gerateClauseSelectForDDLView(query);
        this.gerateClauseFromForDDLView(query);
        this.gerateClauseWhereForDDLView(query);
        this.gerateClauseGroupByForDDLView(query);
        return this.getDdlCreateViewComplete();
    }

    @Override
    protected String getDdlCreateViewComplete() {
        System.out.println("SELECT: " + this.select);
        System.out.println("FROM: " + this.from);
        System.out.println("WHERE: " + this.where);
        System.out.println("GROUP BY: " + this.groupBy);
        System.out.println("ORDER BY: " + this.orderBy);
        String result = this.treatComma(this.select) + " "
                + treatComma(this.from) + " "
                + treatComma(this.where) + " "
                + treatComma(this.groupBy);
        System.out.println("COMPLETA: " + result);
        return result;
    }

    protected void gerateClauseSelectForDDLView(MaterializedView query) {
        this.select = query.getClauseFromSql("select");
        String fields = "";
        if (!this.select.equals("select *")) {
            for (String fieldWhere : query.getAllFields("where")) {
                fields += ", " + fieldWhere;
            }
        }
        this.select = query.getComents() + "\n" + this.select + fields;
        this.groupBy = fields;
    }

    protected void gerateClauseFromForDDLView(MaterializedView query) {
        this.from = query.getClauseFromSql("from");
        for (Table table : query.getTablesSQL()) {
            if (!this.from.contains(table.getName())) {
                this.from += ", " + table.getSchema() + "." + table.getName();
            } else {
                this.from = this.from.replace(table.getName(), table.getSchema() + "." + table.getName());
            }
        }
    }

    protected void gerateClauseGroupByForDDLView(MaterializedView query) {
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

    @Override
    public boolean hasForceClauseGroupBy() {
        return !this.groupBy.trim().isEmpty() && !this.groupBy.trim().equals(",") && (this.select.contains("sum(") || this.select.contains("count("));
    }

//    private String formatClauseSUM(MaterializedView query) {
////        this.select = query.getClauseFromSql("select");
////        ArrayList<String> fieldsSelect = query.getAllFields("select");
////        for (int i = 0; i < fieldsSelect.size(); i++) {
////            String selectBefore = fieldsSelect.get(i);
////            if ((fieldsSelect.get(i).toLowerCase().contains("sum(")) && !fieldsSelect.get(i).toLowerCase().contains("sum(isnull(")) {
////                String fieldTemp = fieldsSelect.get(i).replace("sum(", "sum(isnull(");
////                fieldTemp = fieldTemp.substring(0, fieldTemp.lastIndexOf(")")) + ", 0)" + fieldTemp.substring(fieldTemp.lastIndexOf(")"));
////                fieldsSelect.set(i, fieldTemp);
////            }
////            this.select = this.select.replace(selectBefore, fieldsSelect.get(i));
////        }
//        return this.select;
//    }
}
