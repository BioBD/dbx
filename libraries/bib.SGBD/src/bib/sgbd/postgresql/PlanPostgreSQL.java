/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd.postgresql;

import bib.sgbd.Plan;

/**
 *
 * @author Rafael
 */
public class PlanPostgreSQL extends Plan {

    public PlanPostgreSQL(String plan) {
        super(plan);
    }

    @Override
    public long getCost() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("..") + 2;
            int end = this.getPlan().substring(ini).indexOf(".") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

    @Override
    public long getNumRow() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("rows=") + 5;
            int end = this.getPlan().substring(ini).indexOf(" ") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

    @Override
    public long getSizeRow() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("width=") + 6;
            int end = this.getPlan().substring(ini).indexOf(")") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

}
