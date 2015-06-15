/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import base.MaterializedView;
import bib.base.Base;
import bib.sgbd.SQL;
import drivers.sqlserver.MaterializedViewSQLServer;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Agrawal extends Base {

    private ArrayList<SQL> capturedQueries;
    private final int treshold;
    private int maxTables;

    public Agrawal() {
        this.treshold = Integer.parseInt(prop.getProperty("threshold"));
    }

    public ArrayList<MaterializedView> getWorkloadSelected(ArrayList<SQL> capturedQueries) {
        ArrayList<MaterializedView> result = new ArrayList<>();
        try {
//            this.capturedQueries = capturedQueries;
//            int i = 1;
//            ArrayList<SQL> tables = new ArrayList<>();
//            ArrayList<ArrayList<SQL>> S = new ArrayList<>();
//            ArrayList<SQL> G = this.getTableSubsetBySize(i, tables);
//            S.add(G);
//            while (i < this.maxTables && !G.isEmpty()) {
//                i++;
//                G = this.getTableSubsetBySize(i, S.get(i - 2));
//                if (!G.isEmpty()) {
//                    S.add(G);
//                }
//            }
            for (SQL capturedQuery : capturedQueries) {
                System.out.println(capturedQuery.getSql());
                MaterializedView temp = new MaterializedViewSQLServer(1, 1);

                temp.copy(capturedQuery);
                result.add(temp);
            }
        } catch (Exception e) {
            log.errorPrint(e);
        }
        return result;
    }

    public long TS_Cost(SQL query) {
        return query.getCapture_count() * query.getCost();

    }

    public ArrayList<SQL> getTableSubsetBySize(int size, ArrayList<SQL> tablesCheck) {
        ArrayList<SQL> tableSubset = new ArrayList<>();
        for (SQL workload : this.capturedQueries) {
            if ((workload.getTablesSQL().size() == size) && (this.TS_Cost(workload) >= this.treshold)) {
                if (tablesCheck.isEmpty()) {
                    this.maxTables = workload.getSchemaDataBase().tables.size();
                    tableSubset.add(workload);
                } else {
                    for (SQL table : tablesCheck) {
                        if (workload.haveTableInTableQuery(table.getTablesSQL())) {
                            tableSubset.add(workload);
                        }
                    }

                }
            }
        }
        return tableSubset;
    }

}
