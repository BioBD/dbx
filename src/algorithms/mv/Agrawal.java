/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import base.MaterializedView;
import bib.base.Base;
import bib.sgbd.SQL;
import bib.sgbd.Table;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Agrawal extends Base {

    private ArrayList<MaterializedView> capturedQueries;
    private int maxTables;

    public ArrayList<MaterializedView> getWorkloadSelected(ArrayList<MaterializedView> capturedQueries) {
        try {
            this.capturedQueries = capturedQueries;
            int i = 1;
            ArrayList<SQL> tables = new ArrayList<>();
            ArrayList<ArrayList<SQL>> S = new ArrayList<>();
            ArrayList<SQL> G = this.getTableSubsetBySize(i, tables);
            S.add(G);
            while (i < this.maxTables && !G.isEmpty()) {
                i++;
                G = this.getTableSubsetBySize(i, S.get(i - 2));
                if (!G.isEmpty()) {
                    S.add(G);
                }
            }
        } catch (Exception e) {
            log.errorPrint(e);
        }
        return this.capturedQueries;
    }

    public int TS_Weight(ArrayList<Table> tables) {
        int num_tuples = 0;
        for (Table table : tables) {
            num_tuples += table.getNumberRows();
        }
        return num_tuples;
    }

    public long TS_Cost(SQL query) {
        return query.getCaptureCount() * query.getCost();

    }

    public ArrayList<SQL> getTableSubsetBySize(int size, ArrayList<SQL> tablesCheck) {
        ArrayList<SQL> tableSubset = new ArrayList<>();
        for (SQL workload : this.capturedQueries) {
            if ((workload.getTablesQuery().size() == size) && (this.TS_Cost(workload) >= Integer.valueOf(prop.getProperty("treshold")))) {
                if (tablesCheck.isEmpty()) {
                    this.maxTables = workload.getSchemaDataBase().tables.size();
                    tableSubset.add(workload);
                } else {
                    for (SQL table : tablesCheck) {
                        if (this.haveTableInTableQuery(workload, table.getTablesQuery())) {
                            tableSubset.add(workload);
                        }
                    }

                }
            }
        }
        return tableSubset;
    }

    public boolean haveTableInTableQuery(SQL query1, ArrayList<Table> tables) {
        for (Table tableCheck : tables) {
            if (query1.getTablesQuery().contains(tableCheck)) {
                return true;
            }
        }
        return false;
    }

}
