/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Rafael
 */
public class Index {

    public ArrayList<Column> columns;
    private String typeColumn;
    private String hypotheticalPlan;

    public String getHypotheticalPlan() {
        return hypotheticalPlan;
    }

    public void setHypotheticalPlan(String hypotheticalPlan) {
        this.hypotheticalPlan = hypotheticalPlan;
    }

    public String getTypeColumn() {
        return typeColumn;
    }

    public void setTypeColumn(String typeColumn) {
        this.typeColumn = typeColumn;
    }

    public Index() {
        this.columns = new ArrayList<>();
    }

    public String getName() {
        if (columns.size() > 0) {
            String columnsNames = "";
            String table = "";
            for (int i = 0; i < columns.size(); i++) {
                table = columns.get(i).getTable();
                if (i > 0) {
                    columnsNames += "_";
                }
                columnsNames += columns.get(i).getName();
            }
            String nameOfIndex = "index_ot_" + table + "_" + columnsNames;
            nameOfIndex = nameOfIndex.replace("public.", "");
            return nameOfIndex;
        }
        return "public." + this.hashCode();
    }

    public String getSintaxe() {
        if (columns.size() > 0) {
            String columnsNames = "";
            String table = "";
            for (int i = 0; i < columns.size(); i++) {
                table = columns.get(i).getTable();
                if (i > 0) {
                    columnsNames += ",";
                }
                columnsNames += columns.get(i).getName();
            }
            return "DROP HYPOTHETICAL INDEX IF EXISTS " + this.getName() + "; CREATE HYPOTHETICAL INDEX " + this.getName() + " ON " + table + " (" + columnsNames + ");";
//            return "DROP INDEX IF EXISTS " + this.getName() + ";";
        }
        return "";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.columns);
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
        final Index other = (Index) obj;
        if (!Objects.equals(this.columns, other.columns)) {
            return false;
        }
        return true;
    }

}
